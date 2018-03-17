package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals


class TestArbitraryTypeCollection : Spek({
    context("Config.extract<Family>") {
        it("should return Family") {
            val config = ConfigFactory.parseString("""
                                          |key = {
                                          |  persons = [
                                          |   {
                                          |     name = "foo"
                                          |     age = 20
                                          |   },
                                          |   {
                                          |     name = "bar"
                                          |     age = 25
                                          |   }]
                                          |}   """.trimMargin())
            val family = config.extract<Family>("key")
            assertEquals(family, Family(listOf(Person("foo", 20), Person("bar", 25))))
        }
    }
})

data class Family(val persons: List<Person>)