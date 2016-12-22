package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.Config4kException
import io.github.config4k.extract
import java.time.Duration
import kotlin.reflect.KClass


internal class ArrayReader(clazz: KClass<*>) : Reader<Array<*>>({
    config, path ->
    when (clazz) {
        Int::class -> config.getIntList(path).toTypedArray()
        String::class -> config.getStringList(path).toTypedArray()
        Boolean::class -> config.getBooleanList(path).toTypedArray()
        Double::class -> config.getDoubleList(path).toTypedArray()
        Long::class -> config.getDoubleList(path).toTypedArray()
        Duration::class -> config.getDurationList(path).toTypedArray()
        Config::class -> config.getConfigList(path).toTypedArray()
        List::class -> config.getList(path).map {
            it.atPath("a").extract<List<*>>("a")
        }.toTypedArray()
        Set::class -> config.getList(path).map {
            it.atPath("a").extract<Set<*>>("a")
        }.toTypedArray()
        else -> throw Config4kException.UnSupportedType(clazz)
    }
})