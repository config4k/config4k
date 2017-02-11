package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestArbitraryType : WordSpec() {
    init {
        "Config.extract<Person>" should {
            "return Person" {
                val config = ConfigFactory.parseString("""
                                          |key = {  
                                          |  name = "foo"
                                          |  age = 20
                                          |}""".trimMargin())
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 20)
            }
        }
    }
}

data class Person(val name: String, val age: Int)