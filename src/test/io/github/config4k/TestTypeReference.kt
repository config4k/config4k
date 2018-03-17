package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals

internal class TestTypeReference : Spek({
    describe("TypeReference.genericType") {
        it("should return Int::class") {
            val genericType = object : TypeReference<List<Int>>() {}.genericType()
            assertEquals(listOf(ClassContainer(Int::class)), genericType)
        }

        it("should return List::class, Int::class") {
            val genericType = object : TypeReference<List<List<Int>>>() {}.genericType()
            assertEquals(listOf(ClassContainer(List::class, listOf(ClassContainer(Int::class)))), genericType)
        }
    }
})