package io.github.config4k.readers

import kotlin.reflect.KClass


class SelectReader {
    fun getReader(type: KClass<*>): Reader<*> =
            when (type) {
                Int::class -> IntReader()
                else -> TODO()
            }
}