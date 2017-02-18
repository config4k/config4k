package io.github.config4k

import com.typesafe.config.ConfigValue
import kotlin.reflect.KClass
import kotlin.reflect.memberProperties
import kotlin.reflect.primaryConstructor

internal fun getConfigMap(receiver: Any,
                          clazz: KClass<Any>): Map<String, *> =
        clazz.primaryConstructor!!.parameters.map {
            val parameterName = it.name!!
            parameterName to clazz.memberProperties
                    .find { it.name == parameterName }!!
                    .get(receiver)
                    ?.toConfigValue()
        }.toMap()

internal fun Any.toConfigValue(): ConfigValue {
    val name = "dummy"
    return this.toConfig(name).root()[name]!!
}