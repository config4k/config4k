package io.github.config4k

import com.typesafe.config.Config
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals

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

    describe("TheConfigDestructuringScheme --i.e., config[::property]") {
        withConfig(
            """io.github.config4k {
            |test = "boom"
            |bar = "bar"
            |bat = "bat"
            |x {
            |   foo: "bang"
            |}
            |foo = 1
            |} """.trimMargin()
        ) {
            it("should be work for fields") {
                class X(val foo: String)

                class Testing(config: Config) {
                    val test: String = config[::test]
                    @Key("bar")
                    val bat: String = config[::bat]
                    val bar: String = config[::bar]
                    @Key("x")
                    val x: X = config[::x]

                    fun assert() {
                        assertEquals(test, "boom")
                        assertEquals(bat, "bar")
                        assertEquals(bar, "bar")
                        assertEquals(x.foo, "bang")
                    }
                }
                Testing(this@withConfig).assert()
            }
            it("should have a namespace annotation that allows repositioning the path") {
                @Namespace(key = Key("x"))
                class TestingNsKey(config: Config) {
                    val foo: String = config[::foo]

                    fun assert() {
                        assertEquals(foo, "bang")
                    }
                }

                TestingNsKey(this@withConfig).assert()
            }
        }
    }
})