package io.github.config4k.serializers

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValueType
import io.github.config4k.Config4k
import io.github.config4k.render
import io.github.config4k.toConfig
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.mockk
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConfigSerializerTest {
    @Nested
    inner class SimpleConfigTest {
        @Test
        fun checkConfigDecoding() {
            @Serializable
            data class TestConfig(
                @Contextual val conf: Config,
            )

            val test: TestConfig = Config4k.decodeFromConfig("conf { foo = bar }".toConfig())
            assertThat(test.conf).isNotNull()
            assertThat(test.conf.hasPath("foo")).isTrue()

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<TestConfig>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("conf") }
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThrows<SerializationException> { ConfigSerializer.deserialize(mockk()) }
                .shouldHaveMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableConfigDecoding() {
            @Serializable
            data class TestConfig(
                @Contextual val conf: Config? = null,
            )

            val test: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.conf).isNull()
        }

        @Test
        fun checkDefaultConfigDecoding() {
            @Serializable
            data class TestConfig(
                @Contextual val conf: Config = ConfigFactory.empty(),
            )

            val test: TestConfig = Config4k.decodeFromConfig("conf { foo = bar }".toConfig())
            assertThat(test.conf).isNotNull()
            assertThat(test.conf.hasPath("foo")).isTrue()

            val default: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf).isNotNull()
            assertThat(default.conf.hasPath("foo")).isFalse()
        }

        @Test
        fun checkConfigEncoding() {
            @Serializable
            data class TestConfig(
                @Contextual val conf: Config,
            )

            val test: Config = Config4k.encodeToConfig(TestConfig("foo = bar".toConfig()))
            assertThat(test.hasPath("conf")).isTrue()
            assertThat(test.hasPath("conf.foo")).isTrue()
            assertThat(test.getString("conf.foo")).isEqualTo("bar")
            assertThat(test.render()).isEqualTo("conf{foo=bar}")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThrows<SerializationException> { ConfigSerializer.serialize(mockk(), mockk()) }
                .shouldHaveMessage("This class can be encoded only by Hocon format")
        }
    }

    @Nested
    inner class ListConfigTest {
        @Test
        fun checkListConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: List<@Contextual Config>,
            )

            val emptyTest: TestConfig = Config4k.decodeFromConfig("conf = []".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: TestConfig =
                Config4k.decodeFromConfig("conf = [{ foo = bar }, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf[0].hasPath("foo")).isTrue()
            assertThat(test.conf[1].hasPath("fizz")).isTrue()

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<TestConfig>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("conf") }
        }

        @Test
        fun checkNullableListConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: List<@Contextual Config?>? = null,
            )

            val nullTest: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.conf).isNull()

            val emptyTest: TestConfig = Config4k.decodeFromConfig("conf = []".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: TestConfig =
                Config4k.decodeFromConfig("conf = [{ foo = bar }, null, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(3)
            assertThat(test.conf?.get(0)?.hasPath("foo")).isTrue()
            assertThat(test.conf?.get(1)).isNull()
            assertThat(test.conf?.get(2)?.hasPath("fizz")).isTrue()
        }

        @Test
        fun checkDefaultListConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: List<@Contextual Config> = listOf(ConfigFactory.empty()),
            )

            val test: TestConfig =
                Config4k.decodeFromConfig("conf = [{ foo = bar }, { fizz = buzz }]".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf[0].hasPath("foo")).isTrue()
            assertThat(test.conf[1].hasPath("fizz")).isTrue()

            val default: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf).isNotNull()
            assertThat(default.conf[0].hasPath("foo")).isFalse()
        }

        @Test
        fun checkListConfigEncoding() {
            @Serializable
            data class TestConfig(
                val conf: List<@Contextual Config?>,
            )

            val test: Config =
                Config4k.encodeToConfig(TestConfig(listOf("foo = bar".toConfig(), null)))
            assertThat(test.hasPath("conf")).isTrue()
            assertThat(test.getList("conf")).hasSize(2)
            assertThat(test.getList("conf")[0].valueType()).isEqualTo(ConfigValueType.OBJECT)
            assertThat(test.getList("conf")[0] as ConfigObject).containsKeys("foo")
            assertThat(test.getList("conf")[1].valueType()).isEqualTo(ConfigValueType.NULL)
            assertThat(test.render()).isEqualTo("conf=[{foo=bar},null]")
        }
    }

    @Nested
    inner class MapConfigTest {
        @Test
        fun checkMapConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: Map<String, @Contextual Config>,
            )

            val emptyTest: TestConfig = Config4k.decodeFromConfig("conf = {}".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: TestConfig =
                Config4k.decodeFromConfig(
                    "conf { key1 { foo = bar }, key2 { fizz = buzz } }".toConfig(),
                )
            assertThat(test.conf).isNotNull().hasSize(2).containsOnlyKeys("key1", "key2")
            assertThat(test.conf["key1"]?.hasPath("foo")).isTrue()
            assertThat(test.conf["key2"]?.hasPath("fizz")).isTrue()

            assertThrows<MissingFieldException> { Config4k.decodeFromConfig<TestConfig>("foo = bar".toConfig()) }
                .should { assertThat(it.missingFields).contains("conf") }
        }

        @Test
        fun checkNullableMapConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: Map<String, @Contextual Config?>? = null,
            )

            val nullTest: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.conf).isNull()

            val emptyTest: TestConfig = Config4k.decodeFromConfig("conf = {}".toConfig())
            assertThat(emptyTest.conf).isNotNull().isEmpty()

            val test: TestConfig =
                Config4k.decodeFromConfig("conf = { key1 { foo = bar }, key2 = null, key3 { fizz = buzz } }".toConfig())
            assertThat(test.conf).isNotNull().hasSize(3)
            assertThat(test.conf?.get("key1")?.hasPath("foo")).isTrue()
            assertThat(test.conf?.get("key2")).isNull()
            assertThat(test.conf?.get("key3")?.hasPath("fizz")).isTrue()
        }

        @Test
        fun checkDefaultMapConfigDecoding() {
            @Serializable
            data class TestConfig(
                val conf: Map<String, @Contextual Config> = mapOf("key" to ConfigFactory.empty()),
            )

            val test: TestConfig =
                Config4k.decodeFromConfig("conf = { key1 { foo = bar }, key2 { fizz = buzz } }".toConfig())
            assertThat(test.conf).isNotNull().hasSize(2)
            assertThat(test.conf["key1"]?.hasPath("foo")).isTrue()
            assertThat(test.conf["key2"]?.hasPath("fizz")).isTrue()

            val default: TestConfig = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.conf).isNotNull()
            assertThat(default.conf["key"]?.hasPath("foo")).isFalse()
        }

        @Test
        fun checkMapConfigEncoding() {
            @Serializable
            data class TestConfig(
                val conf: Map<String, @Contextual Config?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    TestConfig(
                        mapOf(
                            "key1" to "foo = bar".toConfig(),
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
