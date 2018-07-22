package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class TestToConfigForArbitraryGenericType : WordSpec({
    "PetPerson>.toConfig" should {
        "return Config having PetPerson<Dog>" {
            val person = PetPerson("foo", 20, Dog("woof")).toConfig("person")
            person.extract<PetPerson<Dog>>("person") shouldBe PetPerson("foo", 20, Dog("woof"))
        }

        "return Config having PetPerson<Yeti>" {
            val person = PetPerson("foo", 20, Yeti).toConfig("person")
            person.extract<PetPerson<Yeti>>("person") shouldBe PetPerson("foo", 20, Yeti)
        }
    }

    "TwoPetPerson.toConfig" should {
        "return Config having TwoPetPerson<Dog, Cat>" {
            val person = TwoPetPerson("foo", 20, Dog("woof"), Cat(7)).toConfig("person")
            person.extract<TwoPetPerson<Dog, Cat>>("person") shouldBe TwoPetPerson("foo", 20, Dog("woof"), Cat(7))
        }

        "return Config having TwoPetPerson<Snake<Mouse>, Cat>" {
            val person = TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")), Cat(7)).toConfig("person")
            person.extract<TwoPetPerson<Snake<Mouse>, Cat>>("person") shouldBe TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")),
                    Cat(7))
        }
    }
})