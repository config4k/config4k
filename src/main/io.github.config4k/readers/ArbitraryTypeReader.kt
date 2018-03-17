package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.extract
import io.github.config4k.getGenericList
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

private fun selectConfig(config: Config, path: String, permitEmpty: Boolean): Config =
        if (permitEmpty &&  path.isEmpty()) {
            config
        } else config.extract(path)

internal class ArbitraryTypeReader(clazz: ClassContainer, override val permitEmptyPath: Boolean = false) : Reader<Any>({ config, path ->
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
                .invoke(selectConfig(config, path, permitEmptyPath), it.name!!)
    }.toMap()
    constructor.callBy(parameters)
})