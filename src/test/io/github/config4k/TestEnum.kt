package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class TestEnum : WordSpec({
    "Config.extract<Size>" should {
        "return SMALL" {
            val config = "key = SMALL".toConfig()
            val small = config.extract<Size>("key")
            small shouldBe Size.SMALL
        }
    }
})

enum class Size {
    SMALL,
    MEDIUM,
    LARGE
}
