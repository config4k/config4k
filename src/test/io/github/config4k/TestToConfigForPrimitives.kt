package io.github.config4k

import io.kotlintest.specs.WordSpec


class TestToConfigForPrimitives : WordSpec() {
    init {
        "10.toConfig" should {
            "return Config having Int value" {
                10.toConfig("key").extract<Int>("key") shouldBe 10
            }
        }

        "str.toConfig" should {
            "return Config having String" {
                "str".toConfig("key").extract<String>("key") shouldBe "str"
            }
        }
    }
}
