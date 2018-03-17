package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals
import java.time.Duration


class TestExtension : Spek({
    describe("Config.extract primitive handling") {
        it("should handle Int value") {
            val num = 0
            val config = ConfigFactory.parseString("""value = $num""")
            assertEquals(num, config.extract<Int>("value"))
        }

        it("should handle String value") {
            val str = "str"
            val config = ConfigFactory.parseString("""value = $str""")
            assertEquals(str, config.extract<String>("value"))
        }

        it("should handle Boolean value") {
            val b = true
            val config = ConfigFactory.parseString("""value = $b""")
            assertEquals(b, config.extract<Boolean>("value"))
        }

        it("should handle Double value") {
            val num = 0.1
            val config = ConfigFactory.parseString("""value = $num""")
            assertEquals(num, config.extract("value"), 0.0)
        }

        it("should handle Long value") {
            val num = 1000L
            val config = ConfigFactory.parseString("""value = $num""")
            assertEquals(num, config.extract("value"))
        }

        it("should handle Duration") {
            val duration = "60minutes"
            val config = ConfigFactory.parseString("""value = $duration""")
            assertEquals(Duration.ofMinutes(60), config.extract<Duration>("value"))
        }

        it("should handle Config") {
            val inner = """
                        |{
                        | field = value
                        |}""".trimMargin()
            val config = ConfigFactory.parseString(
                    """nest = $inner""")
            assertEquals(ConfigFactory.parseString(inner), config.extract<Config>("nest"))
        }

        it("should handle ConfigValue") {
            val b = true
            val config = ConfigFactory.parseString("value = $b")
            val configValue = config.extract<ConfigValue>("value")

            assertEquals(ConfigValueType.BOOLEAN, configValue.valueType())
            assertEquals(b, configValue.unwrapped())
        }
    }
})