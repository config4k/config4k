package io.github.config4k

import com.typesafe.config.ConfigFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import org.junit.Assert.*


class TestNullable : Spek({
    describe("nullability handling") {
        describe("extraction") {
            it("should return T when the value is present") {
                val num = 0
                val config = ConfigFactory.parseString("""key = $num""")
                assertEquals(num, config.extract<Int?>("key"))
            }

            it("should return null when the value is not present") {
                val config = ConfigFactory.parseString("")
                Assert.assertNull(config.extract<Int?>("key"))
            }
        }

        describe("toConfig") {
            it("should omit null values from the config") {
                val complete = PartialData(path1 = "complete", path2 = "complete").toConfig("data")
                assertTrue(complete.hasPath("data.path1"))
                assertTrue(complete.hasPath("data.path2"))

                val partial = PartialData(path1 = "partial").toConfig("data")
                assertTrue(partial.hasPath("data.path1"))
                assertFalse(partial.hasPath("data.path2"))

                val merged = partial.withFallback(complete)
                assertTrue(merged.hasPath("data.path1"))
                assertTrue(merged.hasPath("data.path2"))

                assertEquals("partial", merged.getString("data.path1"))
                assertEquals("complete", merged.getString("data.path2"))
            }
        }
    }
})

data class PartialData(var path1: String? = null, var path2: String? = null)