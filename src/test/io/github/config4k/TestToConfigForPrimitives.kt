package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it


class TestToConfigForPrimitives : Spek({
    context("10.toConfig"){
        it("should return Config having Int value") { 10.toConfig("key").extract<Int>("key") shouldBe 10 }
    }

    context("str.toConfig") {
        it("should return Config having String") {
            "str".toConfig("key").extract<String>("key") shouldBe "str"
        }
    }
})

