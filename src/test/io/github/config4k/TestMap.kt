package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestMap : WordSpec() {
    init {
        "Config.extract<Map<String, T>>" should {
            "return Map<String, T>" {
                val mapConfig =
                        ConfigFactory.parseString("""
                        |nest = {
                        |  key1 = 10
                        |  key2 = 20
                        |}""".trimMargin())
                val list = mapConfig.extract<Map<String, Int>>("nest")
                list shouldBe mapOf("key1" to 10, "key2" to 20)
            }
        }
    }
}