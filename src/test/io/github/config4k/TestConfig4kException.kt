package io.github.config4k

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec


class TestConfig4kException : WordSpec({
    "Config.extract" should {
        "throw Config4kException.UnSupportedType" {
            val config = ConfigFactory.empty()
            shouldThrow<Config4kException.UnSupportedType> {
                config.extract<SomeInterface>("")
            }
        }

        "throw ConfigException.BadPath" {
            val config = ConfigFactory.empty()
            shouldThrow<ConfigException.BadPath> {
                config.extract<Int>("key")
            }
        }

        "throw Config4kException.WrongEnum" {
            val config = """key = foo""".toConfig()
            shouldThrow<Config4kException.WrongEnum> {
                config.extract<Size>("key")
            }.message shouldBe "expected : [SMALL, MEDIUM, LARGE], actually : foo"
        }
    }
})

interface SomeInterface