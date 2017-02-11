package io.github.config4k


import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

/**
 * see [io.github.config4k.readers.SelectReader.getReader]
 */
open class TypeReference<T> {
    fun genericType(): List<KClass<*>>? {
        val type: Type =
                (this.javaClass.genericSuperclass as ParameterizedType)
                        .actualTypeArguments[0]
        return if (type is ParameterizedType) getGenericList(type)
        else null
    }
}

internal fun getGenericList(type: ParameterizedType): List<KClass<*>> {
    val r = if (type.actualTypeArguments.size > 1)
        type.actualTypeArguments[1]
    else type.actualTypeArguments[0]
    val impl = if (r is WildcardType) r.upperBounds[0] else r
    val wild = listOf((if (impl is ParameterizedTypeImpl) impl.rawType
    else impl as Class<*>).kotlin)

    return if (impl is ParameterizedTypeImpl) wild + getGenericList(impl)
    else wild
}
