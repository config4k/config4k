package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


class TestArbitraryType : WordSpec({
        "Config.extract<Person>" should {
            "return Person" {
                val config = """
                    key = {  
                      name = "foo"
                      age = 20
                    }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 20)
            }
            "work if optional argument is omitted" {
                val config = """
                    key = {  
                      name = "foo"
                    }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 10)
            }
            "make optional argument null if there is a key having null" {
                val config = """
                    key = {  
                      name = "foo"
                      age = null
                    }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", null)
            }
        }

        "Config.extract<PrivateEye>" should {
            "return private data class PrivateEye" {
                val config = """
                              key = {
                                target = "criminal"
                              }""".toConfig()
                val person = config.extract<PrivateEye>("key")
                person shouldBe PrivateEye("criminal")
            }
        }

        "Config.extract<Nest>" should {
            "return Nest" {
                val config = """
                    key = {  
                       nest = 1
                       person = {
                         name = "foo"
                         age = 20
                       }
                     }""".toConfig()
                val person = config.extract<Nest>("key")
                person shouldBe Nest(1, Person("foo", 20))
            }
        }

        "Config.extract<WholeConfig>()" should {
            "return WholeConfig without path" {
                val config = """
                    key = {
                      name = "foo"
                      age = 20
                    }""".toConfig()
                val wholeConfig = config.extract<WholeConfig>()
                wholeConfig shouldBe WholeConfig(Person("foo", 20))
            }
        }

    "Config.extract<NestHyphenated>()" should {
        "return NestHyphenated without path" {
            val config = """
                    {
                      nested-person = {
                         name = "foo"
                         age = 20
                       }
                    }""".toConfig()
            val nestHyphenated = config.extract<NestHyphenated>()
            nestHyphenated shouldBe NestHyphenated(Person("foo", 20))
        }
    }
})

data class Person(val name: String, val age: Int? = 10)

private data class PrivateEye(val target: String)

data class Nest(val nest: Int, val person: Person)

data class WholeConfig(val key: Person)
data class NestHyphenated(val nestedPerson: Person)
