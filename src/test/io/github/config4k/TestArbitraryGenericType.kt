package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


class TestArbitraryGenericType : WordSpec({
    "Config.extract<PetPerson<Dog>>" should {
        "return PetPerson" {
            val config = ConfigFactory.parseString("""
                                          |key = {  
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet = {
                                          |     sound = "woof"
                                          |  }
                                          |}""".trimMargin())
            val person = config.extract<PetPerson<Dog>>("key")
            person shouldBe PetPerson("foo", 20, Dog("woof"))
        }
    }

    "Config.extract<PetPerson<Yeti>>" should {
        "return PetPerson" {
            val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  name = "foo"
                                          |  age = 20
                                          |  pet = { }
                                          |}""".trimMargin())
            val person = config.extract<PetPerson<Yeti>>("key")
            person shouldBe PetPerson("foo", 20, Yeti)
        }
    }

    "Config.extract<TwoPetPerson<Dog, Cat>>" should {
        "return TwoPetPerson" {
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
            person shouldBe TwoPetPerson("foo", 20, Dog("woof"), Cat(7))
        }
    }

    "Config.extract<TwoPetPerson<Snake<Mouse>, Cat>>" should {
        "return TwoPetPerson" {
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
            person shouldBe TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")), Cat(7))
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
