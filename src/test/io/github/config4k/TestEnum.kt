package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec


class TestEnum : WordSpec() {
    init {
        "Config.extract<Size>" should {
            "return SMALL" {
                val config = ConfigFactory.parseString("""key = SMALL""")
                val small = config.extract<Size>("key")
                small shouldBe Size.SMALL
            }
        }
    }
}

enum class Size {
    SMALL,
    MEDIUM,
    LARGE
}