package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


class TestToConfigForPrimitives : WordSpec({
    "10.toConfig" should {
        "return Config having Int value" {
            10.toConfig("key").extract<Int>("key") shouldBe 10
        }
    }

    "byte.toConfig" should {
        "return Config having Byte value" {
            10.toByte().toConfig("key").extract<Byte>("key") shouldBe 10.toByte()
        }
    }

    "str.toConfig" should {
        "return Config having String" {
            "str".toConfig("key").extract<String>("key") shouldBe "str"
        }
    }
})
