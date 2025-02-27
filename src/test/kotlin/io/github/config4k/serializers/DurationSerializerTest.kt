package io.github.config4k.serializers

import com.typesafe.config.Config
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class DurationSerializerTest {
    private companion object {
        val OneHour: Duration = 1.hours
        val TenDays: Duration = 10.days
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val p: Duration,
            )

            var test: Conf = Config4k.decodeFromConfig("p = 1s".toConfig())
            assertThat(test.p).isEqualTo(1.seconds)

            test = Config4k.decodeFromConfig("p = 1 s".toConfig())
            assertThat(test.p).isEqualTo(1.seconds)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("p")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val p: Duration? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.p).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val p: Duration = OneHour,
            )

            val test: Conf = Config4k.decodeFromConfig("p = 10d".toConfig())
            assertThat(test.p).isEqualTo(TenDays)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.p).isEqualTo(OneHour)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val p: Duration,
            )

            var test = Config4k.encodeToConfig(Conf(1.nanoseconds))
            assertThat(test.render()).isEqualTo("p=\"1 ns\"")

            test = Config4k.encodeToConfig(Conf(1.microseconds))
            assertThat(test.render()).isEqualTo("p=\"1 us\"")

            test = Config4k.encodeToConfig(Conf(1.milliseconds))
            assertThat(test.render()).isEqualTo("p=\"1 ms\"")

            test = Config4k.encodeToConfig(Conf(1.seconds))
            assertThat(test.render()).isEqualTo("p=\"1 s\"")

            test = Config4k.encodeToConfig(Conf(1.minutes))
            assertThat(test.render()).isEqualTo("p=\"1 m\"")

            test = Config4k.encodeToConfig(Conf(1.hours))
            assertThat(test.render()).isEqualTo("p=\"1 h\"")

            test = Config4k.encodeToConfig(Conf(1.days))
            assertThat(test.render()).isEqualTo("p=\"1 d\"")

            test = Config4k.encodeToConfig(Conf(24.hours))
            assertThat(test.render()).isEqualTo("p=\"1 d\"")

            test = Config4k.encodeToConfig(Conf(25.hours))
            assertThat(test.render()).isEqualTo("p=\"25 h\"")

            test = Config4k.encodeToConfig(Conf(1.hours + 1.nanoseconds))
            assertThat(test.render()).isEqualTo("p=\"3600000000001 ns\"")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: List<Duration>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = [1 h, 10 d]".toConfig())
            assertThat(test.ps).containsExactly(OneHour, TenDays)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: List<Duration?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = [1 h, null, 10 d]".toConfig())
            assertThat(test.ps).containsExactly(OneHour, null, TenDays)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: List<Duration> = listOf(OneHour),
            )

            val test: Conf = Config4k.decodeFromConfig("ps = [1 h, 10 d]".toConfig())
            assertThat(test.ps).containsExactly(OneHour, TenDays)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).containsExactly(OneHour)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: List<Duration?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(OneHour, null)))
            assertThat(test.render()).isEqualTo("ps=[\"1 h\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, Duration>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps { key1 = 1 h, key2 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", OneHour)
                .containsEntry("key2", TenDays)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, Duration?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("ps = { key1 = 1 h, key2 = null, key3 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(3)
                .containsEntry("key1", OneHour)
                .containsEntry("key2", null)
                .containsEntry("key3", TenDays)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, Duration> = mapOf("key" to OneHour),
            )

            val test: Conf = Config4k.decodeFromConfig("ps = { key1 = 1 h, key2 = 10 d }".toConfig())
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", OneHour)
                .containsEntry("key2", TenDays)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).hasSize(1).containsEntry("key", OneHour)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, Duration?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to OneHour,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ps{key1=\"1 h\",key2=null}")
        }
    }
}
