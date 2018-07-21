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
            "work if optional argument is omitted" {
                val config = ConfigFactory.parseString("""
                                          |key = {  
                                          |  name = "foo"
                                          |}""".trimMargin())
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 10)
            }
            "make optional argument null if there is a key having null" {
                val config = ConfigFactory.parseString("""
                                          |key = {  
                                          |  name = "foo"
                                          |  age = null
                                          |}""".trimMargin())
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", null)
            }
        }

        "Config.extract<Nest>" should {
            "return Nest" {
                val config = ConfigFactory.parseString("""
                                          |key = {  
                                          |  nest = 1
                                          |  person = {
                                          |    name = "foo"
                                          |    age = 20
                                          |  }
                                          |}""".trimMargin())
                val person = config.extract<Nest>("key")
                person shouldBe Nest(1, Person("foo", 20))
            }
        }

        "Config.extract<WholeConfig>()" should {
            "return WholeConfig without path" {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |}""".trimMargin())
                val wholeConfig = config.extract<WholeConfig>()
                wholeConfig shouldBe WholeConfig(Person("foo", 20))
            }
        }
    }
}

data class Person(val name: String, val age: Int? = 10)

data class Nest(val nest: Int, val person: Person)

data class WholeConfig(val key: Person)