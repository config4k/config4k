package io.github.config4k

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass

/**
 * see [io.github.config4k.readers.SelectReader.getReader]
 */
public open class TypeReference<T> {
    public fun genericType(): Map<String, ClassContainer> {
        val type: Type =
            (this.javaClass.genericSuperclass as ParameterizedType)
                .actualTypeArguments[0]
        return if (type is ParameterizedType) getGenericMap(type) else emptyMap()
    }
}

public data class ClassContainer(
    val mapperClass: KClass<*>,
    val typeArguments: Map<String, ClassContainer> = emptyMap(),
)

internal fun getGenericMap(
    type: ParameterizedType,
    typeArguments: Map<String, ClassContainer> = emptyMap(),
): Map<String, ClassContainer> {
    val typeParameters = (type.rawType as Class<*>).kotlin.typeParameters
    return type.actualTypeArguments
        .mapIndexed { index, r ->
            val typeParameterName = typeParameters[index].name
            val impl = if (r is WildcardType) r.upperBounds[0] else r
            typeParameterName to
                if (impl is TypeVariable<*>) {
                    requireNotNull(typeArguments[impl.name]) { "no type argument for ${impl.name} found" }
                } else {
                    val wild =
                        ((if (impl is ParameterizedType) impl.rawType else impl) as Class<*>).kotlin
                    if (impl is ParameterizedType) {
                        ClassContainer(wild, getGenericMap(impl, typeArguments))
                    } else {
                        ClassContainer(wild)
                    }
                }
        }.toMap()
}
