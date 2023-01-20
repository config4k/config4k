package io.github.config4k.readers

import io.github.config4k.Config4kException
import io.github.config4k.extract
import kotlin.reflect.KClass

internal class EnumReader(clazz: KClass<*>) : Reader<Enum<*>>({ config, path ->
    stringToEnum(config.extract(path), clazz)
}) {
    companion object {
        fun stringToEnum(value: String, clazz: KClass<*>): Enum<*> =
            clazz.java.enumConstants.let { enumValues ->
                enumValues.find { it.toString().equals(value, true) } as? Enum<*>
                    ?: throw Config4kException.WrongEnum(enumValues.map(Any::toString), value)
            }
    }
}
