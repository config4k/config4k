package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestNullable : WordSpec() {
    init {
        "Config.extract<T?>" should {
            "return T" {
                val num = 0
                val config = ConfigFactory.parseString("""key = $num""")
                config.extract<Int?>("key") shouldBe num
            }

            "return null" {
                val config = ConfigFactory.parseString("")
                config.extract<Int?>("key") shouldBe null
            }
        }

        "Any.toConfig" should {
            "omit null values from the config"{
                val complete = PartialData(path1 = "complete",path2 = "complete").toConfig("data")
                complete.hasPath("data.path1") shouldBe true
                complete.hasPath("data.path2") shouldBe true

                val partial = PartialData(path1 = "partial").toConfig("data")
                partial.hasPath("data.path1") shouldBe true
                partial.hasPath("data.path2") shouldBe false

                val merged = partial.withFallback(complete)
                merged.hasPath("data.path1") shouldBe true
                merged.hasPath("data.path2") shouldBe true

                merged.getString("data.path1") shouldBe "partial"
                merged.getString("data.path2") shouldBe "complete"
            }
        }
    }
}

data class PartialData(var path1: String?= null, var path2: String? = null)