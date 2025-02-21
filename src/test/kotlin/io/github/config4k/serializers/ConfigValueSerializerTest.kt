package io.github.config4k.serializers

import com.typesafe.config.Config
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.ConfigValueType
import com.typesafe.config.ConfigValueType.OBJECT
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import io.github.config4k.toConfigValue
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
import org.assertj.core.api.InstanceOfAssertFactories.MAP
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConfigValueSerializerTest {
    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val conf: ConfigValue,
            )

            val test: Conf = Config4k.decodeFromConfig("conf { foo = bar }".toConfig())
            assertThat(test.conf.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf).asInstanceOf(MAP).containsKey("foo")

            val thrown = catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("conf")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { ConfigValueSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val conf: ConfigValue? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.conf).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val conf: ConfigValue = ConfigValueFactory.fromMap(emptyMap()),
            )

            val test: Conf = Config4k.decodeFromConfig("conf { foo = bar }".toConfig())
            assertThat(test.conf.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf).asInstanceOf(MAP).containsKey("foo")

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf.valueType()).isEqualTo(OBJECT)
            assertThat(default.conf).asInstanceOf(MAP).isEmpty()
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val conf: ConfigValue,
            )

            val test: Config = Config4k.encodeToConfig(Conf(mapOf("foo" to "bar").toConfigValue()))
            assertThat(test.hasPath("conf")).isTrue()
            assertThat(test.hasPath("conf.foo")).isTrue()
            assertThat(test.getString("conf.foo")).isEqualTo("bar")
            assertThat(test.render()).isEqualTo("conf{foo=bar}")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { ConfigValueSerializer.serialize(mockk(), mockk()) }
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
                val conf: List<@Contextual ConfigValue>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("conf = []".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: Conf = Config4k.decodeFromConfig("conf = [{ foo = bar }, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf[0].valueType()).isEqualTo(OBJECT)
            assertThat(test.conf[0]).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf[1].valueType()).isEqualTo(OBJECT)
            assertThat(test.conf[1]).asInstanceOf(MAP).containsKey("fizz")

            val thrown = catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("conf")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val conf: List<@Contextual ConfigValue?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.conf).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("conf = []".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: Conf = Config4k.decodeFromConfig("conf = [{ foo = bar }, null, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(3)
            assertThat(test.conf?.get(0)?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf?.get(0)).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf?.get(1)).isNull()
            assertThat(test.conf?.get(2)?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf?.get(2)).asInstanceOf(MAP).containsKey("fizz")
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val conf: List<@Contextual ConfigValue> = listOf(emptyMap<String, Any>().toConfigValue()),
            )

            val test: Conf = Config4k.decodeFromConfig("conf = [{ foo = bar }, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf[0].valueType()).isEqualTo(OBJECT)
            assertThat(test.conf[0]).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf[1].valueType()).isEqualTo(OBJECT)
            assertThat(test.conf[1]).asInstanceOf(MAP).containsKey("fizz")

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf).isNotNull()
            assertThat(default.conf[0].valueType()).isEqualTo(OBJECT)
            assertThat(default.conf[0]).asInstanceOf(MAP).isEmpty()
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val conf: List<@Contextual ConfigValue?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        listOf(
                            mapOf("foo" to "bar").toConfigValue(),
                            null,
                        ),
                    ),
                )
            assertThat(test.hasPath("conf")).isTrue()
            assertThat(test.getList("conf")).hasSize(2)
            assertThat(test.getList("conf")[0].valueType()).isEqualTo(OBJECT)
            assertThat(test.getList("conf")[0] as ConfigObject).containsKeys("foo")
            assertThat(test.getList("conf")[1].valueType()).isEqualTo(ConfigValueType.NULL)
            assertThat(test.render()).isEqualTo("conf=[{foo=bar},null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val conf: Map<String, @Contextual ConfigValue>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("conf = {}".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: Conf = Config4k.decodeFromConfig("conf { key1 { foo = bar }, key2 { fizz = buzz } }".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2).containsOnlyKeys("key1", "key2")
            assertThat(test.conf["key1"]?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf["key1"]).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf["key2"]?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf["key2"]).asInstanceOf(MAP).containsKey("fizz")

            val thrown = catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("conf")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val conf: Map<String, @Contextual ConfigValue?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.conf).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("conf = {}".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: Conf = Config4k.decodeFromConfig("conf = { key1 { foo = bar }, key2 = null, key3 { fizz = buzz } }".toConfig())
            assertThat(test.conf).isNotNull().hasSize(3)
            assertThat(test.conf?.get("key1")?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf?.get("key1")).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf?.get("key2")).isNull()
            assertThat(test.conf?.get("key3")?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf?.get("key3")).asInstanceOf(MAP).containsKey("fizz")
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val conf: Map<String, @Contextual ConfigValue> =
                    mapOf("key" to ConfigValueFactory.fromMap(emptyMap())),
            )

            val test: Conf = Config4k.decodeFromConfig("conf = { key1 { foo = bar }, key2 { fizz = buzz } }".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf["key1"]?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf["key1"]).asInstanceOf(MAP).containsKey("foo")
            assertThat(test.conf["key2"]?.valueType()).isEqualTo(OBJECT)
            assertThat(test.conf["key2"]).asInstanceOf(MAP).containsKey("fizz")

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf).isNotNull()
            assertThat(default.conf["key"]?.valueType()).isEqualTo(OBJECT)
            assertThat(default.conf["key"]).asInstanceOf(MAP).isEmpty()
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val conf: Map<String, @Contextual ConfigValue?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to mapOf("foo" to "bar").toConfigValue(),
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.hasPath("conf.key1.foo")).isTrue()
            assertThat(test.hasPathOrNull("conf.key2")).isTrue()
            assertThat(test.getIsNull("conf.key2")).isTrue()
            assertThat(test.render()).isEqualTo("conf{key1{foo=bar},key2=null}")
        }
    }
}
