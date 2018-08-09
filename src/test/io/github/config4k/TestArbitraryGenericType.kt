package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


class TestArbitraryGenericType : WordSpec({
    "Config.extract<PetPerson<Dog>>" should {
        "return PetPerson" {
            val config = """
                key = {
                  name = "foo"
                  age = 20
                  pet = {
                    sound = "woof"
                  }
                }""".toConfig()
            val person = config.extract<PetPerson<Dog>>("key")
            person shouldBe PetPerson("foo", 20, Dog("woof"))
        }
    }

    "Config.extract<PetPerson<Yeti>>" should {
        "return PetPerson" {
            val config = """
                key = {
                  name = "foo"
                  age = 20
                  pet = { }
                }""".toConfig()
            val person = config.extract<PetPerson<Yeti>>("key")
            person shouldBe PetPerson("foo", 20, Yeti)
        }
    }

    "Config.extract<TwoPetPerson<Dog, Cat>>" should {
        "return TwoPetPerson" {
            val config = """
                key = {
                  name = "foo"
                  age = 20
                  pet1 = {
                    sound = "woof"
                    }
                  pet2 = {
                    meowTime = 7
                    }
                  }""".toConfig()
            val person = config.extract<TwoPetPerson<Dog, Cat>>("key")
            person shouldBe TwoPetPerson("foo", 20, Dog("woof"), Cat(7))
        }
    }

    "Config.extract<TwoPetSwapPerson<Dog, Cat>>" should {
        "return TwoPetSwapPerson" {
            val config = """
                key = {
                  name = "baz"
                  age = 2
                  firstPet = {
                    sound = "woof"
                    }
                  secondPet = {
                    meowTime = 6
                    }
                  }""".toConfig()
            val person = config.extract<TwoPetSwapPerson<Dog, Cat>>("key")
            person shouldBe TwoPetSwapPerson("baz", 2, Cat(6), Dog("woof"))
        }
    }

    "Config.extract<TwoPetPerson<Snake<Mouse>, Cat>>" should {
        "return TwoPetPerson" {
            val config = """
                key = {
                  name = "foo"
                  age = 20
                  pet1 = {
                     length = 20
                     food = {
                         name = "Joe"
                     }
                  }
                  pet2 = {
                     meowTime = 7
                  }
                }""".toConfig()
            val person = config.extract<TwoPetPerson<Snake<Mouse>, Cat>>("key")
            person shouldBe TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")), Cat(7))
        }
    }

    "Config.extract<PetPerson<HungrySnake<Mouse>, Cat>>" should {
        "return PetPerson" {
            val config = """
                key = {
                  name = "bar"
                  age = 22
                  pet = {
                     length = 40
                     food = [{
                         name = "Joe"
                     }
                     {
                         name = "Mary"
                     }]
                  }
                }""".toConfig()
            val person = config.extract<PetPerson<HungrySnake<Mouse>>>("key")
            person shouldBe PetPerson("bar", 22, HungrySnake(40, listOf(Mouse("Joe"), Mouse("Mary"))))
        }
    }
})

interface Pet

data class PetPerson<out P : Pet>(val name: String, val age: Int, val pet: P)
data class TwoPetPerson<out P1 : Pet, out P2 : Pet>(val name: String, val age: Int, val pet1: P1, val pet2: P2)
data class TwoPetSwapPerson<out P1 : Pet, out P2 : Pet>(val name: String, val age: Int, val secondPet: P2, val firstPet: P1)

data class Dog(val sound: String) : Pet
data class Cat(val meowTime: Int) : Pet
data class Snake<out Food : Pet>(val length: Int, val food: Food) : Pet
data class HungrySnake<out Food : Pet>(val length: Int, val food: List<Food>) : Pet
data class Mouse(val name: String) : Pet
object Yeti : Pet
