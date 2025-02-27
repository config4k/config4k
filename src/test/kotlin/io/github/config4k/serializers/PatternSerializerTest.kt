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
import java.util.regex.Pattern

class PatternSerializerTest {
    private companion object {
        val simplePattern: Pattern = Pattern.compile("[0-9]{10}")
        val patternWithFlags: Pattern =
            Pattern.compile(
                "[0-9]{10}",
                Pattern.LITERAL or Pattern.CASE_INSENSITIVE,
            )

        val patternComparator = nullsLast(compareBy(Pattern::pattern, { it.flags() }))
    }

    @Nested
    inner class SimpleTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Pattern,
            )

            var test: Conf = Config4k.decodeFromConfig("p = \"[0-9]{10}\"".toConfig())
            assertThat(test.p).usingComparator(patternComparator).isEqualTo(simplePattern)

            test = Config4k.decodeFromConfig("p = {flags=[LITERAL, CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}".toConfig())
            assertThat(test.p).usingComparator(patternComparator).isEqualTo(patternWithFlags)

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("p = {flags=[UNKNOWN],pattern=\"[0-9]{10}\"}".toConfig()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("io.github.config4k.serializers.PatternSerializer.PatternFlag does not contain element with name 'UNKNOWN'")

            assertThatThrownBy { Config4k.decodeFromConfig<Conf>("p = 10".toConfig()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("java.util.regex.Pattern can't be specified by NUMBER value type")

            var thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("p")

            thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("p = {flags=[]}".toConfig()) }
            assertThat(thrown.missingFields).contains("pattern")
        }

        @Test
        fun checkIncorrectDecoder() {
            assertThatThrownBy { PatternSerializer.deserialize(mockk()) }
                .isInstanceOf(SerializationException::class.java)
                .hasMessage("This class can be decoded only by Hocon format")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Pattern? = null,
            )

            val test: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(test.p).isNull()
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Pattern = simplePattern,
            )

            val test: Conf = Config4k.decodeFromConfig("p = {flags=[LITERAL, CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}".toConfig())
            assertThat(test.p).usingComparator(patternComparator).isEqualTo(patternWithFlags)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.p).usingComparator(patternComparator).isEqualTo(simplePattern)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                @Contextual val p: Pattern,
            )

            val simple = Config4k.encodeToConfig(Conf(simplePattern))
            assertThat(simple.render()).isEqualTo("p=\"[0-9]{10}\"")

            val withOps = Config4k.encodeToConfig(Conf(patternWithFlags))
            assertThat(withOps.render()).isEqualTo("p{flags=[\"CASE_INSENSITIVE\",LITERAL],pattern=\"[0-9]{10}\"}")
        }

        @Test
        fun checkIncorrectEncoder() {
            assertThatThrownBy { PatternSerializer.serialize(mockk(), mockk()) }
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
                val ps: List<@Contextual Pattern>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig("ps = [\"[0-9]{10}\", {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}]".toConfig())
            assertThat(test.ps).usingElementComparator(patternComparator).containsExactly(simplePattern, patternWithFlags)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Pattern?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = []".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps =  [\"[0-9]{10}\", null, {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}]".toConfig(),
                )
            assertThat(test.ps).usingElementComparator(patternComparator).containsExactly(simplePattern, null, patternWithFlags)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Pattern> = listOf(simplePattern),
            )

            val test: Conf =
                Config4k.decodeFromConfig("ps = [\"[0-9]{10}\", {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}]".toConfig())
            assertThat(test.ps).usingElementComparator(patternComparator).containsExactly(simplePattern, patternWithFlags)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps).usingElementComparator(patternComparator).containsExactly(simplePattern)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: List<@Contextual Pattern?>,
            )

            val test: Config = Config4k.encodeToConfig(Conf(listOf(patternWithFlags, null)))
            assertThat(test.render()).isEqualTo("ps=[{flags=[\"CASE_INSENSITIVE\",LITERAL],pattern=\"[0-9]{10}\"},null]")
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun checkDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Pattern>,
            )

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps { key1 = {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}, key2 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.ps).hasSize(2).containsKeys("key1", "key2")
            assertThat(test.ps.values).usingElementComparator(patternComparator).containsExactlyInAnyOrder(patternWithFlags, simplePattern)

            val thrown =
                catchThrowableOfType(MissingFieldException::class.java) { Config4k.decodeFromConfig<Conf>("foo = bar".toConfig()) }
            assertThat(thrown.missingFields).contains("ps")
        }

        @Test
        fun checkNullableDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Pattern?>? = null,
            )

            val nullTest: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(nullTest.ps).isNull()

            val emptyTest: Conf = Config4k.decodeFromConfig("ps = {}".toConfig())
            assertThat(emptyTest.ps).isEmpty()

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps { key1 = {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}, key2 = null, key3 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.ps?.values)
                .usingElementComparator(patternComparator)
                .containsExactlyInAnyOrder(simplePattern, patternWithFlags, null)
        }

        @Test
        fun checkDefaultDecoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Pattern> = mapOf("key" to simplePattern),
            )

            val test: Conf =
                Config4k.decodeFromConfig(
                    "ps { key1 = {flags=[LITERAL,CASE_INSENSITIVE],pattern=\"[0-9]{10}\"}, key2 = \"[0-9]{10}\" }".toConfig(),
                )
            assertThat(test.ps.values).usingElementComparator(patternComparator).containsExactlyInAnyOrder(simplePattern, patternWithFlags)

            val default: Conf = Config4k.decodeFromConfig("foo = bar".toConfig())
            assertThat(default.ps.values).usingElementComparator(patternComparator).containsExactly(simplePattern)
        }

        @Test
        fun checkEncoding() {
            @Serializable
            data class Conf(
                val ps: Map<String, @Contextual Pattern?>,
            )

            val test: Config =
                Config4k.encodeToConfig(
                    Conf(
                        mapOf(
                            "key1" to patternWithFlags,
                            "key2" to null,
                        ),
                    ),
                )
            assertThat(test.render()).isEqualTo("ps{key1{flags=[\"CASE_INSENSITIVE\",LITERAL],pattern=\"[0-9]{10}\"},key2=null}")
        }
    }
}
