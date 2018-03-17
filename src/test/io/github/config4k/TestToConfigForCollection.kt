package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it


class TestToConfigForCollection : Spek({
    context("List<Person>.toConfig") {
        it("should return Config having list of Person") {
            val list = listOf(Person("foo", 20), Person("bar", 25))
            list.toConfig("list")
                    .extract<List<Person>>("list") shouldBe list
        }
    }

    context("Map<String, Person>.toConfig") {
        it("should return Config having Map<String, Person>") {
            val map = mapOf(
                    "foo" to Person("foo", 20),
                    "bar" to Person("bar", 25))
            map.toConfig("map")
                    .extract<Map<String, Person>>("map") shouldBe map
        }
    }
})
