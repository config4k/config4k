package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it


class TestToConfigForArbitraryType : Spek({
    context("Person.toConfig") {
        it("should return Config having Person") {
            val person = Person("foo", 20).toConfig("person")
            person.extract<Person>("person") shouldBe Person("foo", 20)
        }
    }

    context("Nest.toConfig") {
        it("should return Config having Nest") {
            val nest = Nest(1, Person("foo", 20)).toConfig("nest")
            nest.extract<Nest>("nest") shouldBe
                    Nest(1, Person("foo", 20))
        }
    }

    context("NullableName.toConfig") {
        it("should return Config having name") {
            val person = NullableName("foo").toConfig("nullable")
            person.extract<NullableName>("nullable") shouldBe
                    NullableName("foo")
        }

        it("should return Config having null") {
            val person = NullableName(null).toConfig("nullable")
            person.extract<NullableName>("nullable") shouldBe
                    NullableName(null)
        }
    }

    context("Size.toConfig") {
        it("should return Config having Size") {
            val person = Size.SMALL.toConfig("size")
            person.extract<Size>("size") shouldBe Size.SMALL
        }
    }
})

data class NullableName(val name: String?)