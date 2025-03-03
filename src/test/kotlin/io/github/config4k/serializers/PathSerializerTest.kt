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
import java.nio.file.Path
import java.nio.file.Paths

class PathSerializerTest {
    private companion object {
        val path: Path = Paths.get("/example/file")
        val defaultPath: Path = Paths.get("/default/file")
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Path,
            )

            val test: Conf = Config4k.decodeFromConfig("p = /example/file".toConfig())
            assertThat(test.p).isEqualTo(path)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) {
                    Config4k.decodeFromConfig<Conf>("foo = bar".toConfig())
                }
            assertThat(thrown.missingFields).contains("p")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Path? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.p).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Path = defaultPath,
            )

            val test: Conf = Config4k.decodeFromConfig("p = /example/file".toConfig())
            assertThat(test.p).isEqualTo(path)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.p).isEqualTo(defaultPath)
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { PathSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Path,
            )

            val test = Config4k.encodeToConfig(Conf(path))
            assertThat(test.render()).isEqualTo("p=\"/example/file\"")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { PathSerializer.serialize(mockk(), mockk()) }
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
                val ps: List<@Contextual Path>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps = [/example/file, /default/file]".toConfig(),
                )
            assertThat(test.ps).containsExactly(path, defaultPath)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Path?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps = [/example/file, null, /default/file]".toConfig(),
                )
            assertThat(test.ps).containsExactly(path, null, defaultPath)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Path> = listOf(defaultPath),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps = [/example/file, /default/file]".toConfig(),
                )
            assertThat(test.ps).containsExactly(path, defaultPath)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).containsExactly(defaultPath)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Path?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(path, null)))
            assertThat(test.render()).isEqualTo("ps=[\"/example/file\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Path>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps { key1 = /example/file, key2 = /default/file }".toConfig(),
                )
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", path)
                .containsEntry("key2", defaultPath)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Path?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps { key1 = /example/file, key2 = null, key3 = /default/file }"
                        .toConfig(),
                )
            assertThat(test.ps)
                .hasSize(3)
                .containsEntry("key1", path)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultPath)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Path> = mapOf("key" to defaultPath),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps = { key1 = /example/file, key2 = /default/file }".toConfig(),
                )
            assertThat(test.ps)
                .hasSize(2)
                .containsEntry("key1", path)
                .containsEntry("key2", defaultPath)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).hasSize(1).containsEntry("key", defaultPath)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Path?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to defaultPath,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ps{key1=\"/default/file\",key2=null}")
        }
    }
}
