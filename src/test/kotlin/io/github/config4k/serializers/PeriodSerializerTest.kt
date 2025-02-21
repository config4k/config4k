package io.github.config4k.serializers

import com.typesafe.config.Config
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Period

val OneYear: Period = Period.ofYears(1)
val TenDays: Period = Period.ofDays(10)

class PeriodSerializerTest {
    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Period,
            )

            val test: Conf = Config4k.decodeFromConfig("p = 1 y".toConfig())
            assertThat(test.p).isEqualTo(OneYear)

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("p") }
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThrows<SerializationException> { PeriodSerializer.deserialize(mockk()) }
                .shouldHaveMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Period? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.p).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Period = TenDays,
            )

            val test: Conf = Config4k.decodeFromConfig("p = 1 y".toConfig())
            assertThat(test.p).isEqualTo(OneYear)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.p).isEqualTo(TenDays)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Period,
            )

            var test = Config4k.encodeToConfig(Conf(OneYear))
            assertThat(test.render()).isEqualTo("p=\"1 y\"")
            test = Config4k.encodeToConfig(Conf(Period.ofMonths(10)))
            assertThat(test.render()).isEqualTo("p=\"10 m\"")
            test = Config4k.encodeToConfig(Conf(TenDays))
            assertThat(test.render()).isEqualTo("p=\"10 d\"")
            test = Config4k.encodeToConfig(Conf(Period.of(1, 20, 0)))
            assertThat(test.render()).isEqualTo("p=\"32 m\"")

            assertThrows<SerializationException> { Config4k.encodeToConfig(Conf(Period.of(1, 0, 2))) }
                .shouldHaveMessage("java.time.Period can be specified by only one time unit")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThrows<SerializationException> { PeriodSerializer.serialize(mockk(), mockk()) }
                .shouldHaveMessage("This class can be encoded only by Hocon format")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Period>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = [10 d, 1 y]".toConfig())
            assertThat(test.ps).containsExactly(TenDays, OneYear)

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("ps") }
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Period?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = [10 d, null, 1 year]".toConfig())
            assertThat(test.ps).containsExactly(TenDays, null, OneYear)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Period> = listOf(OneYear),
            )

            val test: Conf = Config4k.decodeFromConfig("ps = [1 year, 10 d]".toConfig())
            assertThat(test.ps).containsExactly(OneYear, TenDays)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).containsExactly(OneYear)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Period?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(OneYear, null)))
            assertThat(test.render()).isEqualTo("ps=[\"1 y\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Period>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps { key1 = 1 y, key2 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", OneYear)
                .containsEntry("key2", TenDays)

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("ps") }
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Period?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = { key1 = 1 y, key2 = null, key3 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(3)
                .containsEntry("key1", OneYear)
                .containsEntry("key2", null)
                .containsEntry("key3", TenDays)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Period> = mapOf("key" to OneYear),
            )

            val test: Conf =
                Config4k.decodeFromConfig("ps = { key1 = 1 y, key2 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", OneYear)
                .containsEntry("key2", TenDays)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).hasSize(1).containsEntry("key", OneYear)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Period?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to OneYear,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ps{key1=\"1 y\",key2=null}")
        }
    }
}
