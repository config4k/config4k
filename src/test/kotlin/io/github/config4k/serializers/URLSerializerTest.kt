package io.github.config4k.serializers

import com.typesafe.config.Config
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URL

class URLSerializerTest {
    private companion object {
        val url: URL = URL("https://www.example.com")
        val defaultUrl: URL = URL("https://www.default.com")
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URL,
            )

            val test: Conf = Config4k.decodeFromConfig("u = \"https://www.example.com\"".toConfig())
            assertThat(test.u).isEqualTo(url)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) {
                    Config4k.decodeFromConfig<Conf>("foo = bar".toConfig())
                }
            assertThat(thrown.missingFields).contains("u")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URL? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.u).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URL = defaultUrl,
            )

            val test: Conf = Config4k.decodeFromConfig("u = \"https://www.example.com\"".toConfig())
            assertThat(test.u).isEqualTo(url)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.u).isEqualTo(defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URL,
            )

            val test = Config4k.encodeToConfig(Conf(url))
            assertThat(test.render()).isEqualTo("u=\"https://www.example.com\"")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URL>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"https://www.example.com\", \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(url, defaultUrl)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URL?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"https://www.example.com\", null, \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(url, null, defaultUrl)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URL> = listOf(defaultUrl),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"https://www.example.com\", \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(url, defaultUrl)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).containsExactly(defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URL?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(url, null)))
            assertThat(test.render()).isEqualTo("us=[\"https://www.example.com\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URL>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = \"https://www.example.com\", key2 = \"https://www.default.com\" }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", url)
                .containsEntry("key2", defaultUrl)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URL?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = \"https://www.example.com\", key2 = null, key3 = \"https://www.default.com\" }"
                        .toConfig(),
                )
            assertThat(test.us)
                .hasSize(3)
                .containsEntry("key1", url)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultUrl)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URL> = mapOf("key" to defaultUrl),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = { key1 = \"https://www.example.com\", key2 = \"https://www.default.com\" }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", url)
                .containsEntry("key2", defaultUrl)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).hasSize(1).containsEntry("key", defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URL?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to defaultUrl,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("us{key1=\"https://www.default.com\",key2=null}")
        }
    }
}
