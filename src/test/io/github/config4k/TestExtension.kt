package io.github.config4k

import com.typesafe.config.*
import io.kotlintest.matchers.exactly
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.time.Duration
import java.time.Period
import java.time.temporal.TemporalAmount


class TestExtension : WordSpec({
    "Config.extract" should {
        "return Int value" {
            val num = 0
            val config = ConfigFactory.parseString("""value = $num""")
            config.extract<Int>("value") shouldBe num
        }

        "return String value" {
            val str = "str"
            val config = ConfigFactory.parseString("""value = $str""")
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

        "return Float value" {
            val num = 0.1f
            val config = ConfigFactory.parseString("""value = $num""")
            config.extract<Float>("value") shouldBe exactly(num)
        }

        "return Long value" {
            val num = 1000L
            val config = ConfigFactory.parseString("""value = $num""")
            config.extract<Long>("value") shouldBe num
        }

        "return ConfigMemorySize" {
            val memorySize = "100KiB"
            val config = ConfigFactory.parseString("""value = $memorySize""")
            config.extract<ConfigMemorySize>("value") shouldBe
                    ConfigMemorySize.ofBytes(100 * 1024)
        }

        "return Duration" {
            val duration = "60minutes"
            val config = ConfigFactory.parseString("""value = $duration""")
            config.extract<Duration>("value") shouldBe
                    Duration.ofMinutes(60)
        }

        "return Period" {
            val period = "10years"
            val config = ConfigFactory.parseString("""value = $period""")
            config.extract<Period>("value") shouldBe
                    Period.ofYears(10)
        }

        "return Regex" {
            val regex = ".*"
            val config = ConfigFactory.parseString("""value = "$regex"""")
            config.extract<Regex>("value").toString() shouldBe ".*"
        }

        "return TemporalAmount" {
            val temporalAmount = "5weeks"
            val config = ConfigFactory.parseString("""value = $temporalAmount""")
            config.extract<TemporalAmount>("value") shouldBe
                    Period.ofWeeks(5)
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
    "Config property delegate" should {
        "return Int value" {
            val num = 0
            val config = ConfigFactory.parseString("""value = $num""")
            val value: Int by config
            value shouldBe num
        }

        "return String value" {
            val str = "str"
            val config = ConfigFactory.parseString("""value = $str""")
            val value: String by config
            value shouldBe str
        }

        "return Boolean value" {
            val b = true
            val config = ConfigFactory.parseString("""value = $b""")
            val value: Boolean by config
            value shouldBe b
        }

        "return Double value" {
            val num = 0.1
            val config = ConfigFactory.parseString("""value = $num""")
            val value: Double by config
            value shouldBe num
        }

        "return Float value" {
            val num = 0.1f
            val config = ConfigFactory.parseString("""value = $num""")
            val value: Float by config
            value shouldBe num
        }

        "return Long value" {
            val num = 1000L
            val config = ConfigFactory.parseString("""value = $num""")
            val value: Long by config
            value shouldBe num
        }

        "return ConfigMemorySize" {
            val memorySize = "100KiB"
            val config = ConfigFactory.parseString("""value = $memorySize""")
            val value: ConfigMemorySize by config
            value shouldBe ConfigMemorySize.ofBytes(100 * 1024)
        }

        "return Duration" {
            val duration = "60minutes"
            val config = ConfigFactory.parseString("""value = $duration""")
            val value: Duration by config
            value shouldBe Duration.ofMinutes(60)
        }

        "return Period" {
            val period = "10years"
            val config = ConfigFactory.parseString("""value = $period""")
            val value: Period by config
            value shouldBe Period.ofYears(10)
        }

        "return Regex" {
            val regex = ".*"
            val config = ConfigFactory.parseString("""value = "$regex"""")
            val value: Regex by config
            value.pattern shouldBe regex
        }

        "return TemporalAmount" {
            val temporalAmount = "5weeks"
            val config = ConfigFactory.parseString("""value = $temporalAmount""")
            val value: TemporalAmount by config
            value shouldBe Period.ofWeeks(5)
        }

        "return Config" {
            val inner = """
                        |{
                        | field = value
                        |}""".trimMargin()
            val config = ConfigFactory.parseString(
                    """nest = $inner""")
            val nest: Config by config
            nest shouldBe ConfigFactory.parseString(inner)
        }

        "return ConfigValue" {
            val b = true
            val config = ConfigFactory.parseString("value = $b")
            val value: ConfigValue by config
            value.valueType() shouldBe ConfigValueType.BOOLEAN
            value.unwrapped() shouldBe b
        }
    }
})

fun String.toConfig(): Config = ConfigFactory.parseString(this.trimIndent())
