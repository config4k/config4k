package io.github.config4k

import io.kotlintest.specs.WordSpec


class TestToConfigForArbitraryType : WordSpec() {
    init {
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
    }
}