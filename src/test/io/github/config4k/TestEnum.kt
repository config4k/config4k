package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals

class TestEnum : Spek({
    describe("Enum extraction") {
        it(" should extract the correct enum") {
            val config = ConfigFactory.parseString("""key = SMALL""")
            val small = config.extract<Size>("key")
            assertEquals(small, Size.SMALL)
        }
    }
})

enum class Size {
    SMALL,
    MEDIUM,
    LARGE
}