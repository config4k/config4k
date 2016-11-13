package io.github.config4k.readers

import kotlin.reflect.KClass


class SelectReader {
    /**
     * Don't use this method.
     * Called by an inline function [io.github.config4k.Extension.extract],
     * this method is public even though it is just for internal.
     *
     * Add new case to support new type.
     *
     * @param type a instance got from the given type by reflection
     */
    fun getReader(type: KClass<*>): Reader<*> =
            when (type) {
                Int::class -> IntReader()
                String::class -> StringReader()
                Boolean::class -> BooleanReader()
                Double::class -> DoubleReader()
                else -> TODO()
            }
}