package io.github.config4k.serializers

import com.typesafe.config.Config
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import io.mockk.mockk
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Period
import java.time.chrono.JapaneseChronology
import java.time.temporal.TemporalAmount

class TemporalAmountSerializerTest {
    private companion object {
        val OneYear: TemporalAmount = Period.ofYears(1)
        val FiveMinutes: Duration = Duration.ofMinutes(5)
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val t: TemporalAmount,
            )

            var test: Conf = Config4k.decodeFromConfig("t = 1 y".toConfig())
            assertThat(test.t).isEqualTo(OneYear)

            test = Config4k.decodeFromConfig("t = 5 m".toConfig())
            assertThat(test.t).isEqualTo(FiveMinutes)

            test = Config4k.decodeFromConfig("t = 5 mo".toConfig())
            assertThat(test.t).isEqualTo(Period.ofMonths(5))

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) {
                    Config4k.decodeFromConfig<Conf>("foo = bar".toConfig())
                }
            assertThat(thrown.missingFields).contains("t")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { TemporalAmountSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val t: TemporalAmount? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.t).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val t: TemporalAmount = OneYear,
            )

            val test: Conf = Config4k.decodeFromConfig("t = 5 m".toConfig())
            assertThat(test.t).isEqualTo(FiveMinutes)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.t).isEqualTo(OneYear)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val t: TemporalAmount,
            )

            var test = Config4k.encodeToConfig(Conf(FiveMinutes))
            assertThat(test.render()).isEqualTo("t=\"5 m\"")
            test = Config4k.encodeToConfig(Conf(Period.ofMonths(10)))
            assertThat(test.render()).isEqualTo("t=\"10 mo\"")
            test = Config4k.encodeToConfig(Conf(OneYear))
            assertThat(test.render()).isEqualTo("t=\"1 y\"")

            assertThatThrownBy { Config4k.encodeToConfig(Conf(JapaneseChronology.INSTANCE.period(10, 12, 0))) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("Class java.time.chrono.ChronoPeriodImpl can't be encoded by Hocon format")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { TemporalAmountSerializer.serialize(mockk(), mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be encoded only by Hocon format")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ts: List<@Contextual TemporalAmount>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ts = []".toConfig())
            assertThat(emptyTest.ts).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ts = [1 y, 5 m]".toConfig())
            assertThat(test.ts).containsExactly(OneYear, FiveMinutes)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ts")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ts: List<@Contextual TemporalAmount?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ts).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ts = []".toConfig())
            assertThat(emptyTest.ts).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ts = [1 y, null, 5 m]".toConfig())
            assertThat(test.ts).containsExactly(OneYear, null, FiveMinutes)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ts: List<@Contextual TemporalAmount> = listOf(FiveMinutes),
            )

            val test: Conf = Config4k.decodeFromConfig("ts = [5 m, 1 y]".toConfig())
            assertThat(test.ts).containsExactly(FiveMinutes, OneYear)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ts).containsExactly(FiveMinutes)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ts: List<@Contextual TemporalAmount?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(FiveMinutes, null)))
            assertThat(test.render()).isEqualTo("ts=[\"5 m\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ts: Map<String, @Contextual TemporalAmount>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ts = {}".toConfig())
            assertThat(emptyTest.ts).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ts { key1 = 5 m, key2 = 1 y }".toConfig())
            assertThat(test.ts)
                .hasSize(2)
                .containsEntry("key1", FiveMinutes)
                .containsEntry("key2", OneYear)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ts")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ts: Map<String, @Contextual TemporalAmount?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ts).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ts = {}".toConfig())
            assertThat(emptyTest.ts).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ts = { key1 = 5 m, key2 = null, key3 = 1 y }".toConfig())
            assertThat(test.ts)
                .hasSize(3)
                .containsEntry("key1", FiveMinutes)
                .containsEntry("key2", null)
                .containsEntry("key3", OneYear)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ts: Map<String, @Contextual TemporalAmount> = mapOf("key" to FiveMinutes),
            )

            val test: Conf =
                Config4k.decodeFromConfig("ts = { key1 = 5 m, key2 = 1 y }".toConfig())
            assertThat(test.ts)
                .hasSize(2)
                .containsEntry("key1", FiveMinutes)
                .containsEntry("key2", OneYear)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ts).hasSize(1).containsEntry("key", FiveMinutes)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ts: Map<String, @Contextual TemporalAmount?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to FiveMinutes,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ts{key1=\"5 m\",key2=null}")
        }
    }
}
