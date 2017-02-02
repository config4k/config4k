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

            "return Map<String, Map<String, T>>" {
                val mapConfig =
                        ConfigFactory.parseString("""
                        |nest = {
                        |  nest1 = {
                        |   key1 = 10
                        |   key2 = 20
                        |}
                        |  nest2 = {
                        |   key3 = 30
                        |   key4 = 40
                        |}
                        |}""".trimMargin())
                val list = mapConfig
                        .extract<Map<String, Map<String, Int>>>("nest")
                list shouldBe mapOf(
                        "nest1" to mapOf("key1" to 10, "key2" to 20),
                        "nest2" to mapOf("key3" to 30, "key4" to 40)
                )
            }
        }
    }
}