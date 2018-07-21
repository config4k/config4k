package io.github.config4k

import com.typesafe.config.Config

interface CustomType {
    fun testParse(clazz: ClassContainer): Boolean
    fun testToConfig(obj: Any): Boolean
    fun parse(clazz: ClassContainer, config: Config, name: String): Any?
    fun toConfig(obj: Any, name: String): Config
}

private val mutableRegistry: MutableList<CustomType> = mutableListOf()

val customTypeRegistry: List<CustomType> = object: List<CustomType> by mutableRegistry {}

fun registerCustomType(customType: CustomType) {
    mutableRegistry.add(customType)
}