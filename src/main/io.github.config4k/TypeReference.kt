package io.github.config4k

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

/**
 * see [io.github.config4k.readers.SelectReader.getReader]
 */
open class TypeReference<T> {
    fun genericType(): KClass<*>? {
        val type: Type =
                (this.javaClass.genericSuperclass as ParameterizedType)
                        .actualTypeArguments[0]
        return if (type is ParameterizedType)
            ((type.actualTypeArguments[0] as WildcardType)
                    .upperBounds[0] as Class<*>).kotlin else null
    }
}