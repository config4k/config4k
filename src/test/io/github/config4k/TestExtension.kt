package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestExtension : WordSpec() {
    init {
        "Config.extract" should {
            "return Int value" {
                val config = ConfigFactory.parseString("number = 0")
                val number = config.extract<Int>("number")
                number shouldBe 0
            }
        }
    }
}