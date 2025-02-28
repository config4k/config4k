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
                @Contextual val url: URL,
            )

            val test: Conf = Config4k.decodeFromConfig("url = \"https://www.example.com\"".toConfig())
            assertThat(test.url).isEqualTo(url)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) {
                    Config4k.decodeFromConfig<Conf>("foo = bar".toConfig())
                }
            assertThat(thrown.missingFields).contains("url")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { URLSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val url: URL? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.url).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val url: URL = defaultUrl,
            )

            val test: Conf = Config4k.decodeFromConfig("url = \"https://www.example.com\"".toConfig())
            assertThat(test.url).isEqualTo(url)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.url).isEqualTo(defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val url: URL,
            )

            val test = Config4k.encodeToConfig(Conf(url))
            assertThat(test.render()).isEqualTo("url=\"https://www.example.com\"")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val urls: List<@Contextual URL>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("urls = []".toConfig())
            assertThat(emptyTest.urls).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls = [\"https://www.example.com\", \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.urls).containsExactly(url, defaultUrl)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("urls")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val urls: List<@Contextual URL?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.urls).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("urls = []".toConfig())
            assertThat(emptyTest.urls).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls = [\"https://www.example.com\", null, \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.urls).containsExactly(url, null, defaultUrl)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val urls: List<@Contextual URL> = listOf(defaultUrl),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls = [\"https://www.example.com\", \"https://www.default.com\"]".toConfig(),
                )
            assertThat(test.urls).containsExactly(url, defaultUrl)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.urls).containsExactly(defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val urls: List<@Contextual URL?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(url, null)))
            assertThat(test.render()).isEqualTo("urls=[\"https://www.example.com\",null]")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { URLSerializer.serialize(mockk(), mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be encoded only by Hocon format")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val urls: Map<String, @Contextual URL>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("urls = {}".toConfig())
            assertThat(emptyTest.urls).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls { key1 = \"https://www.example.com\", key2 = \"https://www.default.com\" }".toConfig(),
                )
            assertThat(test.urls)
                .hasSize(2)
                .containsEntry("key1", url)
                .containsEntry("key2", defaultUrl)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("urls")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val urls: Map<String, @Contextual URL?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.urls).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("urls = {}".toConfig())
            assertThat(emptyTest.urls).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls { key1 = \"https://www.example.com\", key2 = null, key3 = \"https://www.default.com\" }"
                        .toConfig(),
                )
            assertThat(test.urls)
                .hasSize(3)
                .containsEntry("key1", url)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultUrl)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val urls: Map<String, @Contextual URL> = mapOf("key" to defaultUrl),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "urls = { key1 = \"https://www.example.com\", key2 = \"https://www.default.com\" }".toConfig(),
                )
            assertThat(test.urls)
                .hasSize(2)
                .containsEntry("key1", url)
                .containsEntry("key2", defaultUrl)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.urls).hasSize(1).containsEntry("key", defaultUrl)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val urls: Map<String, @Contextual URL?>,
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
            assertThat(test.render()).isEqualTo("urls{key1=\"https://www.default.com\",key2=null}")
        }
    }
}
