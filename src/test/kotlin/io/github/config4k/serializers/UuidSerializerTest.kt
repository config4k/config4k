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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
class UuidSerializerTest {
    private companion object {
        val uuid: Uuid = Uuid.parse("b14240e7-acc0-4a19-bc5d-31a7901e36b0")
        val defaultUuid: Uuid = Uuid.parse("a2e8b949-c5af-45c0-80f7-109ca30a8ba2")
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: Uuid,
            )

            val test: Conf = Config4k.decodeFromConfig("u = b14240e7-acc0-4a19-bc5d-31a7901e36b0".toConfig())
            assertThat(test.u).isEqualTo(uuid)

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
                @Contextual val u: Uuid? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.u).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val u: Uuid = defaultUuid,
            )

            val test: Conf = Config4k.decodeFromConfig("u = b14240e7-acc0-4a19-bc5d-31a7901e36b0".toConfig())
            assertThat(test.u).isEqualTo(uuid)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.u).isEqualTo(defaultUuid)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val u: Uuid,
            )

            val test = Config4k.encodeToConfig(Conf(uuid))
            assertThat(test.render()).isEqualTo("u=b14240e7-acc0-4a19-bc5d-31a7901e36b0")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual Uuid>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [b14240e7-acc0-4a19-bc5d-31a7901e36b0, a2e8b949-c5af-45c0-80f7-109ca30a8ba2]".toConfig(),
                )
            assertThat(test.us).containsExactly(uuid, defaultUuid)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual Uuid?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = []".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [b14240e7-acc0-4a19-bc5d-31a7901e36b0, null, a2e8b949-c5af-45c0-80f7-109ca30a8ba2]".toConfig(),
                )
            assertThat(test.us).containsExactly(uuid, null, defaultUuid)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual Uuid> = listOf(defaultUuid),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = [b14240e7-acc0-4a19-bc5d-31a7901e36b0, a2e8b949-c5af-45c0-80f7-109ca30a8ba2]".toConfig(),
                )
            assertThat(test.us).containsExactly(uuid, defaultUuid)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).containsExactly(defaultUuid)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: List<@Contextual Uuid?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(uuid, null)))
            assertThat(test.render()).isEqualTo("us=[b14240e7-acc0-4a19-bc5d-31a7901e36b0,null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual Uuid>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = b14240e7-acc0-4a19-bc5d-31a7901e36b0, key2 = a2e8b949-c5af-45c0-80f7-109ca30a8ba2 }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", uuid)
                .containsEntry("key2", defaultUuid)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("us")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual Uuid?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.us).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("us = {}".toConfig())
            assertThat(emptyTest.us).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us { key1 = b14240e7-acc0-4a19-bc5d-31a7901e36b0, key2 = null, key3 = a2e8b949-c5af-45c0-80f7-109ca30a8ba2 }"
                        .toConfig(),
                )
            assertThat(test.us)
                .hasSize(3)
                .containsEntry("key1", uuid)
                .containsEntry("key2", null)
                .containsEntry("key3", defaultUuid)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual Uuid> = mapOf("key" to defaultUuid),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "us = { key1 = b14240e7-acc0-4a19-bc5d-31a7901e36b0, key2 = a2e8b949-c5af-45c0-80f7-109ca30a8ba2 }".toConfig(),
                )
            assertThat(test.us)
                .hasSize(2)
                .containsEntry("key1", uuid)
                .containsEntry("key2", defaultUuid)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.us).hasSize(1).containsEntry("key", defaultUuid)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val us: Map<String, @Contextual Uuid?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to defaultUuid,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("us{key1=a2e8b949-c5af-45c0-80f7-109ca30a8ba2,key2=null}")
        }
    }
}
