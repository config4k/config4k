package io.github.config4k

import kotlin.reflect.KClass
import kotlin.reflect.memberProperties
import kotlin.reflect.primaryConstructor

internal fun getConfigMap(receiver: Any,
                          clazz: KClass<Any>): Map<String, Any> =
        clazz.primaryConstructor!!.parameters.map {
            val parameterName = it.name!!
            parameterName to clazz.memberProperties
                    .find { it.name == parameterName }!!
                    .get(receiver)!!
                    .toConfig(parameterName)
                    .root()[parameterName]!!
                    .unwrapped()
        }.toMap()