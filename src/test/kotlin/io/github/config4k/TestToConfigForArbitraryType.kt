package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.net.URI
import java.time.Duration
import java.util.UUID

class TestToConfigForArbitraryType :
    WordSpec({
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

        "DataWithUUID.toConfig" should {
            "return Config having DataWithUUID" {
                val data = DataWithUUID(UUID.fromString("3f5f1d2f-38b7-4a14-9e67-c618d8f83189"))
                val config = data.toConfig("data")
                config.extract<DataWithUUID>("data") shouldBe data
            }
        }

        "DataWithURL.toConfig" should {
            "return Config having DataWithURL" {
                val data = DataWithURL(URI("https://github.com/config4k/config4k").toURL())
                val config = data.toConfig("data")
                config.extract<DataWithURL>("data") shouldBe data
            }
        }

        "DataWithDuration.toConfig" should {
            "return Config having DataWithDuration" {
                val data = DataWithDuration(Duration.ofMinutes(15))
                val config = data.toConfig("data")
                config.extract<DataWithDuration>("data") shouldBe data

                val dataNanos = DataWithDuration(Duration.ofHours(8).plusNanos(123))
                val configNanos = dataNanos.toConfig("data")
                configNanos.extract<DataWithDuration>("data") shouldBe dataNanos
            }
        }
    })

data class NullableName(
    val name: String?,
)

private data class PrivatePerson(
    val name: String,
    val age: Int? = 10,
)
