package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.extract
import io.github.config4k.getGenericList
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType


internal class ArbitraryTypeReader(clazz: ClassContainer) : Reader<Any>({ config, path ->
    val constructor = clazz.mapperClass.primaryConstructor!!
    var typeArgumentIndex = 0
    val parameters = constructor.parameters.map { param ->
        val type = param.type.javaType
        val target = config.extract<Config>(path)
        param to SelectReader.getReader(
                (type as? ParameterizedType)?.let {
                    ClassContainer((it.rawType as Class<*>).kotlin, getGenericList(it))
                } ?: (type as? Class<*>)?.let {
                    ClassContainer(it.kotlin)
                } ?: clazz.typeArguments[typeArgumentIndex++])
                .invoke(target, param.name!!)
    } // if config doesn't have corresponding value, the value is omitted
            .filterNot {
                it.first.isOptional && !config.hasPathOrNull("$path.${it.first.name}")
            }.toMap()
    constructor.callBy(parameters)
})