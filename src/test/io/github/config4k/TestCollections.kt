package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestCollections : WordSpec() {
    init {
        "Config.extract" should {
            "return List" {
                val config = ConfigFactory.parseString("key = [0, 1, 2]")
                val list = config.extract<List<Int>>("key")
                list shouldBe listOf(0, 1, 2)
            }
        }
    }
}