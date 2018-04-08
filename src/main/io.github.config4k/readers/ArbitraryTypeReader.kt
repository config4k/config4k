package io.github.config4k.readers

import io.github.config4k.ClassContainer
import io.github.config4k.extract
import io.github.config4k.getGenericList
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType


internal class ArbitraryTypeReader(clazz: ClassContainer) : Reader<Any>({ config, path ->
    val constructor = clazz.mapperClass.primaryConstructor!!
    var typeArgumentIndex = 0
    val parameters = constructor.parameters.map {
        val type = it.type.javaType
        it to SelectReader.getReader(
                (type as? ParameterizedType)?.let {
                    ClassContainer((it.rawType as Class<*>).kotlin, getGenericList(it))
                } ?: (type as? Class<*>)?.let {
                    ClassContainer(it.kotlin)
                } ?: clazz.typeArguments[typeArgumentIndex++])
                .invoke(config.extract(path), it.name!!)
    } // if config doesn't have corresponding value, the value is omitted
            .filter { it.second != null || !it.first.isOptional }.toMap()
    constructor.callBy(parameters)
})