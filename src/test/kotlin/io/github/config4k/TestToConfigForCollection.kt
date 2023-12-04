package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestToConfigForCollection : WordSpec({
    "List<Person>.toConfig" should {
        "return Config having list of Person" {
            val list = listOf(Person("foo", 20), Person("bar", 25))
            list.toConfig("list")
                .extract<List<Person>>("list") shouldBe list
        }
    }

    "Map<String, Person>.toConfig" should {
        "return Config having Map<String, Person>" {
            val map =
                mapOf(
                    "foo" to Person("foo", 20),
                    "bar" to Person("bar", 25),
                    "@foobar" to Person("bar", 30),
                )
            map.toConfig("map")
                .extract<Map<String, Person>>("map") shouldBe map
        }
    }

    "Map<Int, Person>.toConfig" should {
        "return Config having Map<Int, Person>" {
            val map =
                mapOf(
                    7 to Person("Jon", 12),
                    20 to Person("Doe", 15),
                )
            map.toConfig("map")
                .extract<Map<Int, Person>>("map") shouldBe map
        }
    }
})
