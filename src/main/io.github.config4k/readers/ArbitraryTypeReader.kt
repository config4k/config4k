package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.extract
import io.github.config4k.getGenericList
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType


internal class ArbitraryTypeReader(clazz: ClassContainer) : Reader<Any>({ config, path ->
    extractWithParameters(clazz, config, path)
})

internal fun extractWithParameters(clazz: ClassContainer,
                                   config: Config,
                                   parentPath: String = ""): Any {
    val constructor = clazz.mapperClass.primaryConstructor!!
    var typeArgumentIndex = 0
    val map = constructor.parameters.map { param ->
        val type = param.type.javaType
        param to SelectReader.getReader(
                (type as? ParameterizedType)?.let {
                    ClassContainer((it.rawType as Class<*>).kotlin, getGenericList(it))
                } ?: (type as? Class<*>)?.let {
                    ClassContainer(it.kotlin)
                } ?: clazz.typeArguments[typeArgumentIndex++])
                .invoke(if (parentPath.isEmpty()) config else config.extract(parentPath), param.name!!)
    }
    val parameters = omitValue(map, config, parentPath).toMap()
    return constructor.callBy(parameters)
}

// if config doesn't have corresponding value, the value is omitted
internal fun omitValue(map: List<Pair<KParameter, Any?>>,
                       config: Config,
                       parentPath: String) =
        map.filterNot {
            val path = if (parentPath.isEmpty()) it.first.name
            else "$parentPath.${it.first.name}"
            it.first.isOptional && !config.hasPathOrNull(path)
        }

