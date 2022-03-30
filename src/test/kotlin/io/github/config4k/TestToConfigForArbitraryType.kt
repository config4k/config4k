package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestToConfigForArbitraryType : WordSpec({
    "Person.toConfig" should {
        "return Config having Person" {
            val person = Person("foo", 20).toConfig("person")
            person.extract<Person>("person") shouldBe Person("foo", 20)
        }
    }

    "Nest.toConfig" should {
        "return Config having Nest" {
            val nest = Nest(1, Person("foo", 20)).toConfig("nest")
            nest.extract<Nest>("nest") shouldBe
                Nest(1, Person("foo", 20))
        }
    }

    "NullableName.toConfig" should {
        "return Config having name" {
            val person = NullableName("foo").toConfig("nullable")
            person.extract<NullableName>("nullable") shouldBe
                NullableName("foo")
        }

        "return Config having null" {
            val person = NullableName(null).toConfig("nullable")
            person.extract<NullableName>("nullable") shouldBe
                NullableName(null)
        }
    }

    "Size.toConfig" should {
        "return Config having Size" {
            val person = Size.SMALL.toConfig("size")
            person.extract<Size>("size") shouldBe Size.SMALL
        }
    }

    "PrivatePerson.toConfig" should {
        "return Config having Person" {
            val person = PrivatePerson("foo", 20).toConfig("person")
            person.extract<PrivatePerson>("person") shouldBe PrivatePerson("foo", 20)
        }
    }
})

data class NullableName(val name: String?)

private data class PrivatePerson(val name: String, val age: Int? = 10)
