package io.github.config4k

import com.typesafe.config.Config
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestExtensibleTypes :
    WordSpec({
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

        registerCustomType(HexIntCustomType)
        "value type: Config.extract<HexInt>" should {
            "return HexInt" {
                val config = """
                    key = "CAFE"
                """.trimIndent().toConfig()
                val hex = config.extract<HexInt>("key")
                hex shouldBe HexInt(0xCAFE)
            }
        }
        "value type: Config.extract<ContainerClass>" should {
            "return HexInt" {
                val config = """
                    hex = "CAFE"
                """.trimIndent().toConfig()
                val c = config.extract<ContainerClass>()
                c.hex shouldBe HexInt(0xCAFE)
            }
        }
        "value type: HexInt.toConfig" should {
            "return string value" {
                val config = HexInt(0xBEBE).toConfig("hex")
                config.getString("hex").uppercase() shouldBe "BEBE"
            }
        }
        "value type: ContainerClass.toConfig" should {
            "return string value" {
                val config = ContainerClass(HexInt(0xBEBE)).toConfig("key")
                config.getString("key.hex").uppercase() shouldBe "BEBE"
            }
        }
    })

data class Color(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    fun format(): String = "#${red.toString(16)}${green.toString(16)}${blue.toString(16)}"

    companion object {
        private val regex = Regex("#([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})")

        fun parse(input: String): Color {
            val match =
                regex.matchEntire(input)
                    ?: throw IllegalArgumentException("Input $input not parseable as a color.")

            val r = match.groupValues[1].toInt(16)
            val g = match.groupValues[2].toInt(16)
            val b = match.groupValues[3].toInt(16)

            return Color(r, g, b)
        }
    }
}

object ColorCustomType : CustomType {
    override fun testParse(clazz: ClassContainer): Boolean = clazz.mapperClass == Color::class

    override fun testToConfig(obj: Any): Boolean = Color::class.isInstance(obj)

    override fun parse(
        clazz: ClassContainer,
        config: Config,
        name: String,
    ): Any? = Color.parse(config.getString(name))

    override fun toConfig(
        obj: Any,
        name: String,
    ): Config = (obj as Color).format().toConfig(name)
}

@JvmInline
value class HexInt internal constructor(val rawValue: Int) {

    override fun toString(): String = rawValue.toString(16)

    companion object {
        fun parse(s: String): HexInt = HexInt(s.toInt(16))
    }
}

object HexIntCustomType : CustomType {
    override fun testParse(clazz: ClassContainer): Boolean = clazz.mapperClass == HexInt::class

    override fun testToConfig(obj: Any): Boolean = HexInt::class.isInstance(obj)

    override fun parse(
        clazz: ClassContainer,
        config: Config,
        name: String
    ): Any? = HexInt.parse(config.getString(name))

    override fun toConfig(obj: Any, name: String): Config = (obj as HexInt).toString().toConfig(name)

}
data class ContainerClass(val hex: HexInt)
