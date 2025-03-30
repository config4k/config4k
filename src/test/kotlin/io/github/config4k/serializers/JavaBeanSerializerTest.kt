package io.github.config4k.serializers

import com.typesafe.config.ConfigException.ValidationFailed
import io.github.config4k.Config4k
import io.github.config4k.TestJavaBean
import io.github.config4k.javaBeanSerializer
import io.github.config4k.toConfig
import io.mockk.mockk
import kotlinx.serialization.KSerializer
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

object TestJavaBeanSerializer : KSerializer<TestJavaBean> by javaBeanSerializer()

typealias SerializableTestJavaBean =
    @Serializable(with = TestJavaBeanSerializer::class)
    TestJavaBean

class JavaBeanSerializerTest {
    private companion object {
        val person: TestJavaBean =
            TestJavaBean().apply {
                name = "Alex"
                age = 20
            }
        val defaultPerson: TestJavaBean =
            TestJavaBean().apply {
                name = "John"
                age = 25
            }
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val person: SerializableTestJavaBean,
            )

            val test: Conf = Config4k.decodeFromConfig("person { name = \"Alex\", age = 20 }".toConfig())
            assertThat(test.person).isEqualTo(person)

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("person { name = \"Alex\" }".toConfig()) }
                .isInstanceOf(ValidationFailed::class.java)
                .hasMessage("String: 1: age: No setting at 'age', expecting: number")

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("person { age = 20 }".toConfig()) }
                .isInstanceOf(ValidationFailed::class.java)
                .hasMessage("String: 1: name: No setting at 'name', expecting: string")

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("person")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { TestJavaBeanSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val person: SerializableTestJavaBean? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.person).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val person: SerializableTestJavaBean = defaultPerson,
            )

            val test: Conf =
                Config4k.decodeFromConfig("person = { name = \"Alex\", age = 20 }".toConfig())
            assertThat(test.person).isEqualTo(person)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.person).isEqualTo(defaultPerson)
        }

        @Test
        fun checkEncoding() {
            assertThatThrownBy { TestJavaBeanSerializer.serialize(mockk(), mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("JavaBeanSerializer is only used for deserialization")
        }
    }

    @Nested
    inner class ListTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val persons: List<SerializableTestJavaBean>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("persons = []".toConfig())
            assertThat(emptyTest.persons).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons = [{ name = \"Alex\", age = 20 }, { name = \"John\", age = 25 }]".toConfig(),
                )
            assertThat(test.persons).containsExactly(person, defaultPerson)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("persons")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val persons: List<SerializableTestJavaBean?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.persons).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("persons = []".toConfig())
            assertThat(emptyTest.persons).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons = [{ name = \"Alex\", age = 20 }, null, { name = \"John\", age = 25 }]".toConfig(),
                )
            assertThat(test.persons).containsExactly(person, null, defaultPerson)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val persons: List<SerializableTestJavaBean> = listOf(defaultPerson),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons = [{ name = \"Alex\", age = 20 }, { name = \"John\", age = 25 }]".toConfig(),
                )
            assertThat(test.persons).containsExactly(person, defaultPerson)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.persons).containsExactly(defaultPerson)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val person: List<SerializableTestJavaBean?>,
            )

            assertThatThrownBy { Config4k.encodeToConfig(Conf(listOf(person, null))) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("JavaBeanSerializer is only used for deserialization")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val persons: Map<String, SerializableTestJavaBean>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("persons = {}".toConfig())
            assertThat(emptyTest.persons).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons { key1 = { name = \"Alex\", age = 20 }, key2 = { name = \"John\", age = 25 } }".toConfig(),
                )
            assertThat(test.persons).hasSize(2).containsKeys("key1", "key2")
            assertThat(test.persons.values).containsExactlyInAnyOrder(person, defaultPerson)

            val thrown = catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("persons")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val persons: Map<String, SerializableTestJavaBean?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.persons).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("persons = {}".toConfig())
            assertThat(emptyTest.persons).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons { key1 = { name = \"Alex\", age = 20 }, key2 = null, key3 = { name = \"John\", age = 25 } }".toConfig(),
                )

            assertThat(test.persons).hasSize(3).containsKeys("key1", "key2", "key3")
            assertThat(test.persons?.values).containsExactlyInAnyOrder(person, null, defaultPerson)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val persons: Map<String, SerializableTestJavaBean> = mapOf("key" to defaultPerson),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "persons { key1 = { name = \"Alex\", age = 20 }, key2 = { name = \"John\", age = 25 } }".toConfig(),
                )

            assertThat(test.persons).hasSize(2).containsKeys("key1", "key2")
            assertThat(test.persons.values).containsExactlyInAnyOrder(person, defaultPerson)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())

            assertThat(default.persons).hasSize(1).containsKeys("key")
            assertThat(default.persons.values).containsExactly(defaultPerson)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val person: Map<String, SerializableTestJavaBean?>,
            )
            assertThatThrownBy {
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to defaultPerson,
                            "key2" to null,
                        ),
                    ),
                )
            }.isInstanceOf(SerializationException::class.java)
                .hasMessage("JavaBeanSerializer is only used for deserialization")
        }
    }
}
