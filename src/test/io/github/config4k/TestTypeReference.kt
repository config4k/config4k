package io.github.config4k

import io.kotlintest.specs.WordSpec


internal class TestTypeReference : WordSpec() {
    init {
        "TypeReference.genericType" should {
            "return Int::class" {
                val genericType =
                        object : TypeReference<List<Int>>() {}
                                .genericType()
                genericType shouldBe listOf(ClassContainer(Int::class))
            }

            "return List::class, Int::class" {
                val genericType =
                        object : TypeReference<List<List<Int>>>() {}
                                .genericType()
                genericType shouldBe listOf(ClassContainer(List::class, listOf(ClassContainer(Int::class))))
            }
        }
    }
}