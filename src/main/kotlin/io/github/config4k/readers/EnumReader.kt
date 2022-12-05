package io.github.config4k.readers

import io.github.config4k.Config4kException
import io.github.config4k.extract
import kotlin.reflect.KClass

internal class EnumReader(clazz: KClass<*>) : Reader<Enum<*>>({ config, path ->
    val enumName = config.extract<String>(path)
    val enumConstants = clazz.java.enumConstants
    enumConstants.find { it.toString().equals(enumName, true) } as? Enum<*>
        ?: throw Config4kException
            .WrongEnum(enumConstants.map(Any::toString), enumName)
})
