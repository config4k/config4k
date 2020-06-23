package io.github.config4k

import com.typesafe.config.ConfigValue
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

internal fun getConfigMap(
    receiver: Any,
    clazz: KClass<Any>
): Map<String, *> =
    clazz.primaryConstructor!!.parameters.mapNotNull {
        val parameterName = it.name!!
        clazz.memberProperties
            .find { it.name == parameterName }!!
            .get(receiver)
            ?.let { parameterValue ->
                parameterName to parameterValue.toConfigValue()
            }
    }.toMap()

internal fun Any.toConfigValue(): ConfigValue {
    val name = "dummy"
    return this.toConfig(name).root()[name]!!
}

internal data class MapEntry<K, V>(val key: K, val value: V)
