package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.extract
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType
import kotlin.reflect.primaryConstructor


internal class ArbitraryTypeReader(clazz: KClass<*>) : Reader<Any>({
    config, path ->
    val constructor = clazz.primaryConstructor!!
    val parameters = constructor.parameters.map {
        it to SelectReader.getReader(
                listOf((it.type.javaType as Class<*>).kotlin))(
                config.extract<Config>(path), it.name!!)
    }.toMap()
    constructor.callBy(parameters)
})