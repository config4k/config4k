package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class TestArbitraryType : Spek({
    describe("TestArbitraryType extraction") {
        on("Config.extract<Person>") {
            val config = """|key = {
                            |   name = "foo"
                            |   age = 20
                            |}""".trimMargin()
            withConfig(config) {
                it("should return Person when a path is supplied") { assertEqualsAtPath("key", Person("foo", 20)) }
                it("should return Person when path not supplied") { assertEqualsAfterRepositioning("key", Person("foo", 20)) }
            }
        }

        on("Config.extract<Nest>") {
            val config = """|key = {
                            |  nest = 1
                            |  person = {
                            |    name = "foo"
                            |    age = 20
                            |  }
                            |}""".trimMargin()
            withConfig(config) {
                it("should return Nest when a path is supplied") { assertEqualsAtPath("key", Nest(1, Person("foo", 20))) }
                it("should return Person when a path is not supplied") { assertEqualsAfterRepositioning("key", Nest(1, Person("foo", 20))) }
            }
        }
    }
})

data class Person(val name: String, val age: Int)

data class Nest(val nest: Int, val person: Person)