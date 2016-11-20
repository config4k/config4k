package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestConfig4kException : WordSpec() {
    init {
        "Config.extract" should {
            "throw Config4kException." {
                val config = ConfigFactory.empty()
                shouldThrow<Config4kException.UnSupportedType> {
                    config.extract<SomeInterface>("")
                }
            }
        }
    }
}

interface SomeInterface