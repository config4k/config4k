package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TestArbitraryTypeCollection :
    WordSpec({
        "Config.extract<Family>" should {
            "return Family" {
                val config =
                    """
                key = {
                  persons = [
                   {
                     name = "foo"
                     age = 20
                   },
                   {
                     name = "bar"
                     age = 25
                   }]
                }""".toConfig()
                val family = config.extract<Family>("key")
                family shouldBe Family(listOf(Person("foo", 20), Person("bar", 25)))
            }
        }
    })

data class Family(
    val persons: List<Person>,
)
