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

class RegexSerializerTest {
    private companion object {
        val simpleRegex: Regex = Regex("[0-9]{10}")
        val regexWithOps: Regex = Regex("[0-9]{10}", setOf(RegexOption.LITERAL, RegexOption.IGNORE_CASE))

        val regexComparator = nullsLast(compareBy(Regex::pattern, { it.toPattern().flags() }))
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val r: Regex,
            )

            var test: Conf = Config4k.decodeFromConfig("r = \"[0-9]{10}\"".toConfig())
            assertThat(test.r).usingComparator(regexComparator).isEqualTo(simpleRegex)

            test = Config4k.decodeFromConfig("r = {options=[LITERAL, IGNORE_CASE],pattern=\"[0-9]{10}\"}".toConfig())
            assertThat(test.r).usingComparator(regexComparator).isEqualTo(regexWithOps)

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("r = {options=[UNKNOWN],pattern=\"[0-9]{10}\"}".toConfig()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("kotlin.text.RegexOption does not contain element with name 'UNKNOWN'")

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("r = 10".toConfig()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("kotlin.text.Regex can't be specified by NUMBER value type")

            var thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("r")

            thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("r = {options=[]}".toConfig()) }
            assertThat(thrown.missingFields).contains("pattern")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { RegexSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val r: Regex? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.r).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val r: Regex = simpleRegex,
            )

            val test: Conf = Config4k.decodeFromConfig("r = {options=[LITERAL, IGNORE_CASE],pattern=\"[0-9]{10}\"}".toConfig())
            assertThat(test.r).usingComparator(regexComparator).isEqualTo(regexWithOps)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.r).usingComparator(regexComparator).isEqualTo(simpleRegex)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val r: Regex,
            )

            val simple = Config4k.encodeToConfig(Conf(simpleRegex))
            assertThat(simple.render()).isEqualTo("r=\"[0-9]{10}\"")

            val withOps = Config4k.encodeToConfig(Conf(regexWithOps))
            assertThat(withOps.render()).isEqualTo("r{options=[\"IGNORE_CASE\",LITERAL],pattern=\"[0-9]{10}\"}")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { RegexSerializer.serialize(mockk(), mockk()) }
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
                val rs: List<@Contextual Regex>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("rs = []".toConfig())
            assertThat(emptyTest.rs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig("rs = [\"[0-9]{10}\", {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}]".toConfig())
            assertThat(test.rs).usingElementComparator(regexComparator).containsExactly(simpleRegex, regexWithOps)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("rs")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val rs: List<@Contextual Regex?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.rs).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("rs = []".toConfig())
            assertThat(emptyTest.rs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig("rs =  [\"[0-9]{10}\", null, {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}]".toConfig())
            assertThat(test.rs).usingElementComparator(regexComparator).containsExactly(simpleRegex, null, regexWithOps)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val rs: List<@Contextual Regex> = listOf(simpleRegex),
            )

            val test: Conf =
                Config4k.decodeFromConfig("rs = [\"[0-9]{10}\", {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}]".toConfig())
            assertThat(test.rs).usingElementComparator(regexComparator).containsExactly(simpleRegex, regexWithOps)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.rs).usingElementComparator(regexComparator).containsExactly(simpleRegex)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val rs: List<@Contextual Regex?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(regexWithOps, null)))
            assertThat(test.render()).isEqualTo("rs=[{options=[\"IGNORE_CASE\",LITERAL],pattern=\"[0-9]{10}\"},null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val rs: Map<String, @Contextual Regex>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("rs = {}".toConfig())
            assertThat(emptyTest.rs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "rs { key1 = {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}, key2 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.rs).hasSize(2).containsKeys("key1", "key2")
            assertThat(test.rs.values).usingElementComparator(regexComparator).containsExactlyInAnyOrder(regexWithOps, simpleRegex)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("rs")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val rs: Map<String, @Contextual Regex?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.rs).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("rs = {}".toConfig())
            assertThat(emptyTest.rs).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "rs { key1 = {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}, key2 = null, key3 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.rs?.values).usingElementComparator(regexComparator).containsExactlyInAnyOrder(simpleRegex, regexWithOps, null)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val rs: Map<String, @Contextual Regex> = mapOf("key" to simpleRegex),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "rs { key1 = {options=[LITERAL,IGNORE_CASE],pattern=\"[0-9]{10}\"}, key2 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.rs.values).usingElementComparator(regexComparator).containsExactlyInAnyOrder(simpleRegex, regexWithOps)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.rs.values).usingElementComparator(regexComparator).containsExactly(simpleRegex)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Regex?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to regexWithOps,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ps{key1{options=[\"IGNORE_CASE\",LITERAL],pattern=\"[0-9]{10}\"},key2=null}")
        }
    }
}
