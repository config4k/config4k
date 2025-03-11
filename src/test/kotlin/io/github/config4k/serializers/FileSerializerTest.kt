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
import java.io.File
import java.net.URI

class FileSerializerTest {
    private companion object {
        val file = File("/example/file")
        val defaultFile = File("/default/file")
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val f: File,
            )

            val test: Conf = Config4k.decodeFromConfig("f = /example/file".toConfig())
            assertThat(test.f).isEqualTo(file)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("f")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val f: File? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.f).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val f: File = defaultFile,
            )

            val test: Conf = Config4k.decodeFromConfig("f = /example/file".toConfig())
            assertThat(test.f).isEqualTo(file)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.f).isEqualTo(defaultFile)
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { FileSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val f: File,
            )

            var test = Config4k.encodeToConfig(Conf(file))
            assertThat(test.render()).isEqualTo("f=\"/example/file\"")

            test = Config4k.encodeToConfig(Conf(File("parent", "child")))
            assertThat(test.render()).isEqualTo("f=\"parent/child\"")

            test = Config4k.encodeToConfig(Conf(File(File("parent"), "child")))
            assertThat(test.render()).isEqualTo("f=\"parent/child\"")

            test = Config4k.encodeToConfig(Conf(File(URI("file:/example/file"))))
            assertThat(test.render()).isEqualTo("f=\"/example/file\"")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { FileSerializer.serialize(mockk(), mockk()) }
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
                val fs: List<@Contextual File>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("fs = []".toConfig())
            assertThat(emptyTest.fs).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("fs = [/example/file, /default/file]".toConfig())
            assertThat(test.fs).containsExactly(file, defaultFile)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("fs")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val fs: List<@Contextual File?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.fs).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("fs = []".toConfig())
            assertThat(emptyTest.fs).isEmpty()

            val test: Conf = Config4k.decodeFromConfig("fs = [/example/file, null]".toConfig())
            assertThat(test.fs).containsExactly(file, null)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val fs: List<@Contextual File> = listOf(defaultFile),
            )

            val test: Conf = Config4k.decodeFromConfig("fs = [/example/file]".toConfig())
            assertThat(test.fs).containsExactly(file)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.fs).containsExactly(defaultFile)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val fs: List<@Contextual File?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(file, null)))
            assertThat(test.render()).isEqualTo("fs=[\"/example/file\",null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val fs: Map<String, @Contextual File>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("fs = {}".toConfig())
            assertThat(emptyTest.fs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig("fs { key1 = /example/file, key2 = /default/file }".toConfig())
            assertThat(test.fs)
                .hasSize(2)
                .containsEntry("key1", file)
                .containsEntry("key2", defaultFile)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("fs")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val fs: Map<String, @Contextual File?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.fs).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("fs = {}".toConfig())
            assertThat(emptyTest.fs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig("fs = { key1 = /example/file, key2 = null, key3 = /default/file }".toConfig())
            assertThat(test.fs)
                .hasSize(3)
                .containsEntry("key1", file)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultFile)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val fs: Map<String, @Contextual File> = mapOf("key" to defaultFile),
            )

            val test: Conf = Config4k.decodeFromConfig("fs = { key1 = /example/file }".toConfig())
            assertThat(test.fs)
                .hasSize(1)
                .containsEntry("key1", file)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.fs).hasSize(1).containsEntry("key", defaultFile)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val fs: Map<String, @Contextual File?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to file,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("fs{key1=\"/example/file\",key2=null}")
        }
    }
}
