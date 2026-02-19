package io.github.config4k

import com.typesafe.config.Config
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestJvmInlineValueClass :
    WordSpec({
        registerCustomType(HexInt)
        "value type: Config.extract<HexInt>" should {
            "return HexInt" {
                val config =
                    """
                    key = "CAFE"
                    """.trimIndent().toConfig()
                val hex = config.extract<HexInt>("key")
                hex shouldBe HexInt(0xCAFE)
            }
        }
        "value type: Config.extract<JvmInlineContainer>" should {
            "return HexInt" {
                val config =
                    """
                    hex = "CAFE"
                    """.trimIndent().toConfig()
                val c = config.extract<JvmInlineContainer>()
                c.hex shouldBe HexInt(0xCAFE)
            }
        }
        "value type: HexInt.toConfig" should {
            "return string value" {
                val config = HexInt(0xBEBE).toConfig("hex")
                config.getString("hex").uppercase() shouldBe "BEBE"
            }
        }
        "value type: JvmInlineContainer.toConfig" should {
            "return string value" {
                val config = JvmInlineContainer(HexInt(0xBEBE)).toConfig("key")
                config.getString("key.hex").uppercase() shouldBe "BEBE"
            }
        }
    })

@JvmInline
value class HexInt internal constructor(
    val rawValue: Int,
) {
    override fun toString(): String = rawValue.toString(16)

    companion object : CustomType {
        override fun testParse(clazz: ClassContainer): Boolean = clazz.mapperClass == HexInt::class

        override fun testToConfig(obj: Any): Boolean = HexInt::class.isInstance(obj)

        override fun parse(
            clazz: ClassContainer,
            config: Config,
            name: String,
        ): Any? = HexInt(config.getString(name).toInt(16))

        override fun toConfig(
            obj: Any,
            name: String,
        ): Config = (obj as HexInt).toString().toConfig(name)
    }
}

data class JvmInlineContainer(
    val hex: HexInt,
)
