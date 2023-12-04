package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestMap : WordSpec({
    "Config.extract<Map<String, T>>" should {
        "return Map<String, T>" {
            val mapConfig =
                """
                nest = {
                  key1 = 10
                  key2 = 20
                }""".toConfig()
            val list = mapConfig.extract<Map<String, Int>>("nest")
            list shouldBe mapOf("key1" to 10, "key2" to 20)
        }

        "return Map<String, Map<String, T>>" {
            val mapConfig =
                """
                nest = {
                  nest1 = {
                    key1 = 10
                    key2 = 20
                  }
                  nest2 = {
                    key3 = 30
                    key4 = 40
                  }
                }""".toConfig()
            val list =
                mapConfig
                    .extract<Map<String, Map<String, Int>>>("nest")
            list shouldBe
                mapOf(
                    "nest1" to mapOf("key1" to 10, "key2" to 20),
                    "nest2" to mapOf("key3" to 30, "key4" to 40),
                )
        }

        "return Map<String, List<T>>" {
            val mapConfig =
                """
                nest = {
                  key1 = [0, 1]
                  key2 = [2, 3]
                }""".toConfig()
            val list = mapConfig.extract<Map<String, List<Int>>>("nest")
            list shouldBe mapOf("key1" to listOf(0, 1), "key2" to listOf(2, 3))
        }

        "return Map<Int, String>" {
            val mapConfig =
                """
                nest = [{
                  key = 10
                  value = "dogs"
                },
                {
                  key = 20
                  value = "cats"
                }]""".toConfig()
            val list = mapConfig.extract<Map<Int, String>>("nest")
            list shouldBe mapOf(10 to "dogs", 20 to "cats")
        }
        "return map with non-null values when a key contains dot" {
            val mapConfig =
                """
                nest = {
                  "key1.with.dot" = 10
                  "key2.with.dot" = 20
                }""".toConfig()
            val list = mapConfig.extract<Map<String, Int>>("nest")
            list shouldBe mapOf("key1.with.dot" to 10, "key2.with.dot" to 20)
        }
    }

    "Config.extract<MutableMap<String, T>>" should {
        "return MutableMap<String, T>" {
            val mapConfig =
                """
                nest = {
                  key1 = 10
                  key2 = 20
                }""".toConfig()
            val map = mapConfig.extract<MutableMap<String, Int>>("nest")
            map shouldBe mapOf("key1" to 10, "key2" to 20)
            map["key3"] = 30
            map shouldBe mapOf("key1" to 10, "key2" to 20, "key3" to 30)
        }
    }

    "Config.extract<Map<Size, T>>" should {
        "return Map<Size, T>" {
            val mapConfig =
                """
                nest = {
                  Small = 1
                  MEDIUM = 35
                  large = 42
                }""".toConfig()
            val map = mapConfig.extract<Map<Size, Int>>("nest")
            map shouldBe mapOf(Size.SMALL to 1, Size.MEDIUM to 35, Size.LARGE to 42)
        }
    }
})
