package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it

class TestToConfigForArbitraryGenericType : Spek({
    context("PetPerson>.toConfig") {
        it("should return Config having PetPerson<Dog>") {
            val person = PetPerson("foo", 20, Dog("woof")).toConfig("person")
            person.extract<PetPerson<Dog>>("person") shouldBe PetPerson("foo", 20, Dog("woof"))
        }

        it("should return Config having PetPerson<Yeti>") {
            val person = PetPerson("foo", 20, Yeti).toConfig("person")
            person.extract<PetPerson<Yeti>>("person") shouldBe PetPerson("foo", 20, Yeti)
        }
    }

    context("TwoPetPerson.toConfig") {
        it("should return Config having TwoPetPerson<Dog, Cat>") {
            val person = TwoPetPerson("foo", 20, Dog("woof"), Cat(7)).toConfig("person")
            person.extract<TwoPetPerson<Dog, Cat>>("person") shouldBe TwoPetPerson("foo", 20, Dog("woof"), Cat(7))
        }

        it("should return Config having TwoPetPerson<Snake<Mouse>, Cat>") {
            val person = TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")), Cat(7)).toConfig("person")
            person.extract<TwoPetPerson<Snake<Mouse>, Cat>>("person") shouldBe TwoPetPerson("foo", 20, Snake(20, Mouse("Joe")),
                    Cat(7))
        }
    }
})