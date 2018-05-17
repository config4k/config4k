package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it

object TestUtilities : Spek({
    val fixture = withConfig("""io.github.config4k { test = "boom" } """) { this }

    context("Config.forPackageOf<T>()") {
        it("should be able to descend into a config object using the package of a class") {
            fixture.forPackageOf<TestUtilities>().also {
                val test: String = it.getString("test")
                assert(test == "boom")
            }
        }
    }

    context("Config.extractByPackage<T>(...)") {
        data class TestClass(val test: String)
        it("should be able to extract for an empty path") {
            fixture.extractByPackage<TestClass>().also {
                assert(it.test == "boom")
            }
        }
        it("should be able to extract subPaths") {
            withConfig("""io.github.config4k { bang { test = "boom" } } """) {
                extractByPackage<TestClass>("bang").also {
                    assert(it.test == "boom")
                }
            }
        }
    }
})