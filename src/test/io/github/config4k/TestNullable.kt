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
    }
}