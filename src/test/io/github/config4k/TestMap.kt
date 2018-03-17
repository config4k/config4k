package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it


class TestMap : Spek({
    context("Config.extract<Map<String, T>>") {
        it("should return Map<String, T>") {
            val mapConfig =
                    ConfigFactory.parseString("""
                        |nest = {
                        |  key1 = 10
                        |  key2 = 20
                        |}""".trimMargin())
            val list = mapConfig.extract<Map<String, Int>>("nest")
            list shouldBe mapOf("key1" to 10, "key2" to 20)
        }

        it("should return Map<String, Map<String, T>>") {
            val mapConfig =
                    ConfigFactory.parseString("""
                        |nest = {
                        |  nest1 = {
                        |    key1 = 10
                        |    key2 = 20
                        |  }
                        |  nest2 = {
                        |    key3 = 30
                        |    key4 = 40
                        |  }
                        |}""".trimMargin())
            val list = mapConfig
                    .extract<Map<String, Map<String, Int>>>("nest")
            list shouldBe mapOf(
                    "nest1" to mapOf("key1" to 10, "key2" to 20),
                    "nest2" to mapOf("key3" to 30, "key4" to 40)
            )
        }

        it("should return Map<String, List<T>>") {
            val mapConfig =
                    ConfigFactory.parseString("""
                        |nest = {
                        |  key1 = [0, 1]
                        |  key2 = [2, 3]
                        |}""".trimMargin())
            val list = mapConfig.extract<Map<String, List<Int>>>("nest")
            list shouldBe mapOf("key1" to listOf(0, 1), "key2" to listOf(2, 3))
        }
    }
})