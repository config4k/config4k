package io.github.config4k

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.Row3
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.FencedCodeBlock
import org.commonmark.parser.Parser
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class TestDocumentedExamples : StringSpec({
    "examples should compile and run with expected output" {
        forall(*examples()) { _, script, expected ->
            val output = outputOf {
                engine.eval(
                    """
                    import com.typesafe.config.*
                    import io.github.config4k.*

                    $script
                    """.trimIndent()
                )
            }

            output.lines().sanitized() shouldBe expected.lines().sanitized()
        }
    }
})

private fun List<String>.sanitized(): List<String> = this
    // ignore leading spaces since expected outputs derived from comments may have them
    .map { it.trim() }
    // ignore warnings that Java 13 injects into actual output
    .filterNot { it.startsWith("warning: ") }
    // ignore trailing newline
    .dropLastWhile { it.isBlank() }

private val engine = KotlinJsr223JvmLocalScriptEngineFactory()
    .scriptEngine

private fun examples(): Array<Row3<String, String, String>> = File("README.md")
    .fencedCodeBlocks()
    .groupedByCodeSample()
    .toRows()
    .filter { (lang, _, _) -> lang == "kotlin" }
    .toTypedArray()

private fun File.fencedCodeBlocks(): List<FencedCodeBlock> = readText()
    .let { text ->
        mutableListOf<FencedCodeBlock>()
            .also { blocks ->
                Parser.builder().build().parse(text).accept(object : AbstractVisitor() {
                    override fun visit(fencedCodeBlock: FencedCodeBlock) {
                        blocks += fencedCodeBlock
                    }
                })
            }
    }

private fun List<FencedCodeBlock>.groupedByCodeSample(): List<List<FencedCodeBlock>> =
    mutableListOf<MutableList<FencedCodeBlock>>()
        .also { grouped ->
            forEach { block ->
                when {
                    block.info.isNotBlank() -> grouped.add(mutableListOf(block))
                    block.info.isBlank() -> grouped.lastOrNull()?.add(block)
                }
            }
        }
        .map { it.toList() }

private fun List<List<FencedCodeBlock>>.toRows(): List<Row3<String, String, String>> = map { group ->
    val lang = group.first().info
    val script = group.first().literal
    val output = when (group.size) {
        1 ->
            group.first().literal
                .lines()
                .map { it.substringAfter("//", "") }
                .filter { it.isNotBlank() }
                .joinToString(separator = System.lineSeparator())
        else -> group.drop(1).joinToString(separator = System.lineSeparator()) { it.literal }
    }
    Row3(lang, script, output)
}

private fun outputOf(block: () -> Unit): String {
    val out = System.out
    try {
        val bytes = ByteArrayOutputStream()
        System.setOut(PrintStream(bytes))

        block.invoke()
        System.out.flush()

        return bytes.toString("UTF-8")
    } finally {
        System.setOut(out)
    }
}
