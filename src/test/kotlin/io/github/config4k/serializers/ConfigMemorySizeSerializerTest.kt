package io.github.config4k.serializers

import com.typesafe.config.ConfigMemorySize
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import io.kotest.matchers.should
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConfigMemorySizeSerializerTest {
    private companion object {
        val OneMiB: ConfigMemorySize = ConfigMemorySize.ofBytes(1 * 1024 * 1024)
        val TenKiB: ConfigMemorySize = ConfigMemorySize.ofBytes(10 * 1024)
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val size: ConfigMemorySize,
            )

            val test: Conf = Config4k.decodeFromConfig("size = 1 MiB".toConfig())
            assertThat(test.size).isEqualTo(OneMiB)

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("size") }
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val size: ConfigMemorySize? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.size).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val size: ConfigMemorySize = TenKiB,
            )

            val test: Conf = Config4k.decodeFromConfig("size = 1 MiB".toConfig())
            assertThat(test.size).isEqualTo(OneMiB)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.size).isEqualTo(TenKiB)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val size: ConfigMemorySize,
            )

            var test = Config4k.encodeToConfig(Conf(OneMiB))
            assertThat(test.render()).isEqualTo("size=\"1 MiB\"")
            test = Config4k.encodeToConfig(Conf(TenKiB))
            assertThat(test.render()).isEqualTo("size=\"10 KiB\"")
        }
    }
}
