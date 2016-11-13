package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestExtension : WordSpec() {
    init {
        "Config.extract" should {
            "return Int value" {
                val config = ConfigFactory.parseString("""value = 0""")
                config.extract<Int>("value") shouldBe 0
            }

            "return String value" {
                val config = ConfigFactory.parseString("""value = "str" """)
                config.extract<String>("value") shouldBe "str"
            }

            "return Boolean value" {
                val config = ConfigFactory.parseString("""value = true""")
                config.extract<Boolean>("value") shouldBe true
            }

            "return Double value" {
                val config = ConfigFactory.parseString("""value = 0.1""")
                config.extract<Double>("value") shouldBe exactly(0.1)
            }
        }
    }
}