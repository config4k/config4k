package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestExtension : WordSpec() {
    init {
        "Config.getInt" should {
            "return Int value" {
                val config = ConfigFactory.parseString(
                        "number = 0")
                config.getInt("number") shouldBe 0
            }
        }
    }
}