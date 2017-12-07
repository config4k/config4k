package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.extract
import io.github.config4k.getGenericList
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType


internal class ArbitraryTypeReader(clazz: KClass<*>) : Reader<Any>({
    config, path ->
    val constructor = clazz.primaryConstructor!!
    val parameters = constructor.parameters.map {
        val type = it.type.javaType
        it to SelectReader.getReader(
                (type as? ParameterizedType)?.let {
                    listOf((it.rawType as Class<*>).kotlin) +
                            getGenericList(it)
                } ?: listOf((type as Class<*>).kotlin))
                .invoke(config.extract<Config>(path), it.name!!)
    }.toMap()
    constructor.callBy(parameters)
})