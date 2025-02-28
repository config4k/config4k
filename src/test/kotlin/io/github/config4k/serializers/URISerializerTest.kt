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
                @Contextual val u: URI,
            )

            val test: Conf = Config4k.decodeFromConfig("u = \"example://192.0.2.16:22/\"".toConfig())
            assertThat(test.u).isEqualTo(uri)

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
                @Contextual val u: URI? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.u).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URI = defaultUri,
            )

            val test: Conf = Config4k.decodeFromConfig("u = \"example://192.0.2.16:22/\"".toConfig())
            assertThat(test.u).isEqualTo(uri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.u).isEqualTo(defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val u: URI,
            )

            val test = Config4k.encodeToConfig(Conf(uri))
            assertThat(test.render()).isEqualTo("u=\"example://192.0.2.16:22/\"")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URI>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"example://192.0.2.16:22/\", \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(uri, defaultUri)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URI?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"example://192.0.2.16:22/\", null, \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(uri, null, defaultUri)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URI> = listOf(defaultUri),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [\"example://192.0.2.16:22/\", \"default://192.0.2.16:22/\"]".toConfig(),
                )
            assertThat(test.us).containsExactly(uri, defaultUri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).containsExactly(defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual URI?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(uri, null)))
            assertThat(test.render()).isEqualTo("us=[\"example://192.0.2.16:22/\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URI>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = \"example://192.0.2.16:22/\", key2 = \"default://192.0.2.16:22/\" }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", uri)
                .containsEntry("key2", defaultUri)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URI?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = \"example://192.0.2.16:22/\", key2 = null, key3 = \"default://192.0.2.16:22/\" }"
                        .toConfig(),
                )
            assertThat(test.us)
                .hasSize(3)
                .containsEntry("key1", uri)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultUri)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URI> = mapOf("key" to defaultUri),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = { key1 = \"example://192.0.2.16:22/\", key2 = \"default://192.0.2.16:22/\" }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", uri)
                .containsEntry("key2", defaultUri)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).hasSize(1).containsEntry("key", defaultUri)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual URI?>,
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
            assertThat(test.render()).isEqualTo("us{key1=\"default://192.0.2.16:22/\",key2=null}")
        }
    }
}
