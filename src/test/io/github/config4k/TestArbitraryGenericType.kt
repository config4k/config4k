package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.Assert


class TestArbitraryGenericType : Spek({
    describe("ArbitraryGenericType extraction") {
        on("Config.extract<PetPerson<Dog>>") {
            it("should return PetPerson") {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet = {
                                          |     sound = "woof"
                                          |  }
                                          |}""".trimMargin())
                val person = config.extract<PetPerson<Dog>>("key")
                Assert.assertEquals(person, PetPerson("foo", 20, Dog("woof")))
            }
        }
        on("Config.extract<PetPerson<Yeti>>") {
            it("should return PetPerson") {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet = { }
                                          |}""".trimMargin())
                val person = config.extract<PetPerson<Yeti>>("key")
                Assert.assertEquals(person, PetPerson("foo", 20, Yeti))
            }
        }
        on("Config.extract<TwoPetPerson<Dog, Cat>>") {
            it("should return TwoPetPerson") {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet1 = {
                                          |     sound = "woof"
                                          |  }
                                          |  pet2 = {
                                          |     meowTime = 7
                                          |  }
                                          |}""".trimMargin())
                val person = config.extract<TwoPetPerson<Dog, Cat>>("key")
                Assert.assertEquals(person, TwoPetPerson("foo", 20, Dog("woof"), Cat(7)))
            }
        }



        on("Config.extract<TwoPetPerson<Dog, Cat>>") {
            it("should return TwoPetPerson") {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet1 = {
                                          |     sound = "woof"
                                          |  }
                                          |  pet2 = {
                                          |     meowTime = 7
                                          |  }
                                          |}""".trimMargin())
                val person = config.extract<TwoPetPerson<Dog, Cat>>("key")
                Assert.assertEquals(person, TwoPetPerson("foo", 20, Dog("woof"), Cat(7)))
            }
        }


        on("Config.extract<TwoPetPerson<Snake<Mouse>, Cat>>") {
            it("should return TwoPetPerson") {
                val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet1 = {
                                          |     length = 20
                                          |     food = {
                                          |         name = "Joe"
                                          |     }
                                          |  }
                                          |  pet2 = {
                                          |     meowTime = 7
                                          |  }
                                          |}""".trimMargin())
                val person = config.extract<TwoPetPerson<Snake<Mouse>, Cat>>("key")
                Assert.assertEquals(person, TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")), Cat(7)))
            }
        }
    }
})

interface Pet

data class PetPerson<out P : Pet>(val name: String, val age: Int, val pet: P)
data class TwoPetPerson<out P1 : Pet, out P2 : Pet>(val name: String, val age: Int, val pet1: P1, val pet2: P2)

data class Dog(val sound: String) : Pet
data class Cat(val meowTime: Int) : Pet
data class Snake<out Food : Pet>(val length: Int, val food: Food) : Pet
data class Mouse(val name: String) : Pet
object Yeti : Pet
