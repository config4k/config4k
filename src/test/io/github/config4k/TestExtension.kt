package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType
import io.kotlintest.specs.WordSpec


class TestExtension : WordSpec() {
    init {
        "Config.extract" should {
            "return Int value" {
                val num = 0
                val config = ConfigFactory.parseString("""value = $num""")
                config.extract<Int>("value") shouldBe num
            }

            "return String value" {
                val str = "str"
                val config = ConfigFactory.parseString("""value = $str """)
                config.extract<String>("value") shouldBe str
            }

            "return Boolean value" {
                val b = true
                val config = ConfigFactory.parseString("""value = $b""")
                config.extract<Boolean>("value") shouldBe b
            }

            "return Double value" {
                val num = 0.1
                val config = ConfigFactory.parseString("""value = $num""")
                config.extract<Double>("value") shouldBe exactly(num)
            }

            "return Long value" {
                val num = 1000L
                val config = ConfigFactory.parseString("""value = $num""")
                config.extract<Long>("value") shouldBe num
            }

            "return Config" {
                val inner = """
                        |{
                        | field = value
                        |}""".trimMargin()
                val config = ConfigFactory.parseString(
                        """nest = $inner""")
                config.extract<Config>(
                        "nest") shouldBe ConfigFactory.parseString(inner)
            }

            "return ConfigValue" {
                val b = true
                val config = ConfigFactory.parseString("value = $b")
                val configValue = config.extract<ConfigValue>("value")
                configValue.valueType() shouldBe ConfigValueType.BOOLEAN
                configValue.unwrapped() shouldBe b
            }
        }
    }
}