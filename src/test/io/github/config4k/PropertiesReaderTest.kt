package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.assertThrows
import java.util.*


object PropertiesReaderTest : Spek({

    context("Config.extract<Properties>") {
        val fixture = ConfigFactory.parseString("""
                key = {
                    monkey = "string"
                    donkey = 1
                    zebra = null
                }
            """.trimIndent())
        val expected = Properties().also {
            it["monkey"] = "string"
            it["donkey"] = "1"
            // null values have zero value semantics for props.
            it["zebra"] = ""
        }

        it("should return Properties") { fixture.assertEqualsAtPath("key", expected) }
        it("should return Properties without path specified") { fixture.assertEqualsAfterRepositioning("key", expected) }

        val nestedFixture = ConfigFactory.parseString("""
                 key = {
                    monkey = "string"
                    donkey = 1
                    zebra = null
                    key = {
                        monkey = "string"
                        donkey = 1
                        zebra = null
                    }
                }
        """.trimIndent())

        it("should not accept nested config shapes") {
            assertThrows<Config4kException.InvalidShape> {
                nestedFixture.extract<Properties>()
            }
        }
    }

    data class WithProperties(val id: String, val props: Properties)

    context("Config.extract<WithProperties>") {
        val fixture = ConfigFactory.parseString("""
            key = {
                id = "boom"
                props = {
                    monkey = "string"
                    donkey = 1
                    zebra = null
                }
            }
            """.trimIndent())

        val expected = WithProperties(id = "boom", props = mapOf(
            "monkey" to "string",
            "donkey" to "1",
            // null values have zero value semantics for props.
            "zebra" to  ""
            ).toProperties()
        )
        it("should return WithProperties") { fixture.assertEqualsAtPath("key", expected) }
        it("should return WithProperties without path specified") { fixture.assertEqualsAfterRepositioning("key", expected) }
    }
})
