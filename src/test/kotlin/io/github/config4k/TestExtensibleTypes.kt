package io.github.config4k

import com.typesafe.config.Config
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestExtensibleTypes : WordSpec({
    registerCustomType(ColorCustomType)
    "Config.extract<Color>" should {
        "return Color" {
            val config =
                """
                key = "#FF3389"
                """.toConfig()
            val color = config.extract<Color>("key")
            color shouldBe Color(0xFF, 0x33, 0x89)
        }
    }
    "Color.toConfig" should {
        "return string" {
            val color = Color(0xFE, 0x22, 0x2E)
            val config = color.toConfig("key")
            config.getString("key") shouldBe "#fe222e"
        }
    }
})

data class Color(val red: Int, val green: Int, val blue: Int) {
    fun format(): String {
        return "#${red.toString(16)}${green.toString(16)}${blue.toString(16)}"
    }

    companion object {
        private val regex = Regex("#([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})")
        fun parse(input: String): Color {
            val match = regex.matchEntire(input)
                ?: throw IllegalArgumentException("Input $input not parseable as a color.")

            val r = match.groupValues[1].toInt(16)
            val g = match.groupValues[2].toInt(16)
            val b = match.groupValues[3].toInt(16)

            return Color(r, g, b)
        }
    }
}

object ColorCustomType : CustomType {
    override fun testParse(clazz: ClassContainer): Boolean {
        return clazz.mapperClass == Color::class
    }

    override fun testToConfig(obj: Any): Boolean {
        return Color::class.isInstance(obj)
    }

    override fun parse(clazz: ClassContainer, config: Config, name: String): Any? {
        return Color.parse(config.getString(name))
    }

    override fun toConfig(obj: Any, name: String): Config {
        return (obj as Color).format().toConfig(name)
    }
}
