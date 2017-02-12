package io.github.config4k

import io.kotlintest.specs.WordSpec


class TestToConfig : WordSpec() {
    init {
        "10.toConfig" should {
            "return Config having Int value" {
                10.toConfig("key").extract<Int>("key") shouldBe 10
            }
        }
    }
}
