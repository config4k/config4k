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
import java.net.URI

class URISerializerTest {
    private companion object {
        val uri: URI = URI("example://192.0.2.16:22/")
        val defaultUri: URI = URI("default://192.0.2.16:22/")
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val uri: URI,
            )

            val test: Conf = Config4k.decodeFromConfig("uri = \"example://192.0.2.16:22/\"".toConfig())
            assertThat(test.uri).isEqualTo(uri)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) {
                    Config4k.decodeFromConfig<Conf>("foo = bar".toConfig())
                }
            assertThat(thrown.missingFields).contains("uri")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { URISerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val uri: URI? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.uri).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val uri: URI = defaultUri,
            )

            val test: Conf = Config4k.decodeFromConfig("uri = \"example://192.0.2.16:22/\"".toConfig())
            assertThat(test.uri).isEqualTo(uri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.uri).isEqualTo(defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val uri: URI,
            )

            val test = Config4k.encodeToConfig(Conf(uri))
            assertThat(test.render()).isEqualTo("uri=\"example://192.0.2.16:22/\"")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { URISerializer.serialize(mockk(), mockk()) }
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
                val uris: List<@Contextual URI>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("uris = []".toConfig())
            assertThat(emptyTest.uris).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris = [\"example://192.0.2.16:22/\", \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.uris).containsExactly(uri, defaultUri)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("uris")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val uris: List<@Contextual URI?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.uris).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("uris = []".toConfig())
            assertThat(emptyTest.uris).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris = [\"example://192.0.2.16:22/\", null, \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.uris).containsExactly(uri, null, defaultUri)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val uris: List<@Contextual URI> = listOf(defaultUri),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris = [\"example://192.0.2.16:22/\", \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.uris).containsExactly(uri, defaultUri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.uris).containsExactly(defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val uris: List<@Contextual URI?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(uri, null)))
            assertThat(test.render()).isEqualTo("uris=[\"example://192.0.2.16:22/\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val uris: Map<String, @Contextual URI>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("uris = {}".toConfig())
            assertThat(emptyTest.uris).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris { key1 = \"example://192.0.2.16:22/\", key2 = \"default://192.0.2.16:22/\" }".toConfig(),
                )
            assertThat(test.uris)
                .hasSize(2)
                .containsEntry("key1", uri)
                .containsEntry("key2", defaultUri)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("uris")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val uris: Map<String, @Contextual URI?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.uris).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("uris = {}".toConfig())
            assertThat(emptyTest.uris).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris { key1 = \"example://192.0.2.16:22/\", key2 = null, key3 = \"default://192.0.2.16:22/\" }"
                        .toConfig(),
                )
            assertThat(test.uris)
                .hasSize(3)
                .containsEntry("key1", uri)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultUri)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val uris: Map<String, @Contextual URI> = mapOf("key" to defaultUri),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "uris = { key1 = \"example://192.0.2.16:22/\", key2 = \"default://192.0.2.16:22/\" }".toConfig(),
                )
            assertThat(test.uris)
                .hasSize(2)
                .containsEntry("key1", uri)
                .containsEntry("key2", defaultUri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.uris).hasSize(1).containsEntry("key", defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val uris: Map<String, @Contextual URI?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to defaultUri,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("uris{key1=\"default://192.0.2.16:22/\",key2=null}")
        }
    }
}
