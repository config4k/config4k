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
    }
}

data class NullableName(val name: String?)