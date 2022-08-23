package io.github.config4k

import com.typesafe.config.Config

public interface CustomType {
    public fun testParse(clazz: ClassContainer): Boolean
    public fun testToConfig(obj: Any): Boolean
    public fun parse(clazz: ClassContainer, config: Config, name: String): Any?
    public fun toConfig(obj: Any, name: String): Config
}

private val mutableRegistry: MutableList<CustomType> = mutableListOf()

public val customTypeRegistry: List<CustomType> = object : List<CustomType> by mutableRegistry {}

public fun registerCustomType(customType: CustomType) {
    mutableRegistry.add(customType)
}
