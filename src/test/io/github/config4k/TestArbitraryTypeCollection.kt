package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestArbitraryTypeCollection : WordSpec() {
    init {
        "Config.extract<Family>" should {
            "return Family" {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  persons = [
                                          |   {
                                          |     name = "foo"
                                          |     age = 20
                                          |   },
                                          |   {
                                          |     name = "bar"
                                          |     age = 25
                                          |   }]
                                          |}   """.trimMargin())
                val family = config.extract<Family>("key")
                family shouldBe
                        Family(listOf(Person("foo", 20), Person("bar", 25)))
            }
        }
    }
}

data class Family(val persons: List<Person>)