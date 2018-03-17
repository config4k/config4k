package io.github.config4k

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals
import org.opentest4j.AssertionFailedError


class TestConfig4kException : Spek({
    describe("Config.extract") {
        it("should throw Config4kException.UnSupportedType when attempting to extract an interface") {
            shouldThrow<Config4kException.UnSupportedType> {
                ConfigFactory.empty().extract<SomeInterface>("a.path")
            }
        }
        it("should throw ConfigException.BadPath when the path does not exist") {
            val config = ConfigFactory.parseString("")
            shouldThrow<ConfigException.BadPath> {
                config.extract<Int>("key")
            }
        }
        it("should throw Config4kException.WrongEnum when the value is not part of the enum") {
            val config = ConfigFactory.parseString("""key = foo""")
            val ex = shouldThrow<Config4kException.WrongEnum> {
                config.extract<Size>("key")
            }
            assertEquals(ex.message, "expected : [SMALL, MEDIUM, LARGE], actually : foo")
        }
    }
})

interface SomeInterface
