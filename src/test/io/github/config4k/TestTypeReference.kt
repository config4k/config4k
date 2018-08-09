package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


internal class TestTypeReference : WordSpec({
    "TypeReference.genericType" should {
        "return Int::class" {
            val genericType =
                    object : TypeReference<List<Int>>() {}
                            .genericType()
            genericType shouldBe mapOf("E" to ClassContainer(Int::class))
        }

        "return List::class, Int::class" {
            val genericType =
                    object : TypeReference<List<List<Int>>>() {}
                            .genericType()
            genericType shouldBe mapOf("E" to ClassContainer(List::class, mapOf("E" to ClassContainer(Int::class))))
        }
    }
})
