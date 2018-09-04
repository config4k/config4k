package io.github.config4k


import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

/**
 * see [io.github.config4k.readers.SelectReader.getReader]
 */
open class TypeReference<T> {
    fun genericType(): List<ClassContainer> {
        val type: Type =
                (this.javaClass.genericSuperclass as ParameterizedType)
                        .actualTypeArguments[0]
        return if (type is ParameterizedType) getGenericList(type) else emptyList()
    }
}

data class ClassContainer(val mapperClass: KClass<*>, val typeArguments: List<ClassContainer> = emptyList())

internal fun getGenericList(type: ParameterizedType): List<ClassContainer> {
    return type.actualTypeArguments.toList().map { r ->
        val impl = if (r is WildcardType) r.upperBounds[0] else r
        val wild = (if (impl is ParameterizedType) impl.rawType as Class<*> else impl as Class<*>)
            .kotlin
        if (impl is ParameterizedType) ClassContainer(wild, getGenericList(impl))
        else ClassContainer(wild)
    }
}
