package io.github.config4k

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

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

        "return Double::class, Int::class" {
            val genericType =
                object : TypeReference<GenericTestClass<Double, Int>>() {}
                    .genericType()
            genericType shouldBe mapOf("T1" to ClassContainer(Double::class), "T2" to ClassContainer(Int::class))
        }

        "find container type in list" {
            val clazz =
                ClassContainer(GenericTestClass::class, mapOf("T1" to ClassContainer(Long::class), "T2" to ClassContainer(Float::class)))
            val constructor = GenericTestClass::class.primaryConstructor!!
            val param = constructor.parameters[2] // List<Float>
            val type = param.type.javaType as ParameterizedType

            val genericType = getGenericMap(type, clazz.typeArguments)

            genericType shouldBe mapOf("E" to ClassContainer(Float::class))
        }

        "find nested container type in map" {
            val clazz =
                ClassContainer(GenericTestClass::class, mapOf("T1" to ClassContainer(Long::class), "T2" to ClassContainer(Float::class)))
            val constructor = GenericTestClass::class.primaryConstructor!!
            val param = constructor.parameters[3] // Map<String, List<Long>>
            val type = param.type.javaType as ParameterizedType

            val genericType = getGenericMap(type, clazz.typeArguments)

            genericType shouldBe mapOf(
                "K" to ClassContainer(String::class),
                "V" to ClassContainer(List::class, mapOf("E" to ClassContainer(Long::class)))
            )
        }

        "throw exception when clazz container type arguments don't match" {
            val clazz =
                ClassContainer(GenericTestClass::class, mapOf("T1" to ClassContainer(Long::class), "T" to ClassContainer(Float::class)))
            val constructor = GenericTestClass::class.primaryConstructor!!
            val param = constructor.parameters[2] // List<Float>
            val type = param.type.javaType as ParameterizedType

            val exception = shouldThrow<IllegalArgumentException> {
                getGenericMap(type, clazz.typeArguments)
            }

            exception.message shouldBe "no type argument for T2 found"
        }
    }
})

data class GenericTestClass<T1 : Number, T2 : Number>(
    val number1: T2,
    val number2: T1,
    val list: List<T2>,
    val map: Map<String, List<T1>>
)
