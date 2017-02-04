package io.github.config4k

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestConfig4kException : WordSpec() {
    init {
        "Config.extract" should {
            "throw Config4kException.UnSupportedType" {
                val config = ConfigFactory.empty()
                shouldThrow<Config4kException.UnSupportedType> {
                    config.extract<SomeInterface>("")
                }
            }

            "return ConfigException.BadPath" {
                val config = ConfigFactory.parseString("")
                shouldThrow<ConfigException.BadPath> {
                    config.extract<Int>("key")
                }
            }
        }
    }
}

interface SomeInterface