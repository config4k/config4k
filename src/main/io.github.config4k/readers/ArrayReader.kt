package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.Config4kException
import io.github.config4k.extract
import java.time.Duration
import kotlin.reflect.KClass

internal class ArrayReader(clazz: KClass<*>) : Reader<Array<*>>({ config, path ->
    when (clazz) {
        Int::class -> config.extract<List<Int>>(path).toTypedArray()
        String::class -> config.extract<List<String>>(path).toTypedArray()
        Boolean::class -> config.extract<List<Boolean>>(path).toTypedArray()
        Double::class -> config.extract<List<Double>>(path).toTypedArray()
        Long::class -> config.extract<List<Long>>(path).toTypedArray()
        Duration::class -> config.extract<List<Duration>>(path).toTypedArray()
        Config::class -> config.extract<List<Config>>(path).toTypedArray()
        List::class -> config.extract<List<List<*>>>(path).toTypedArray()
        Set::class -> config.extract<List<Set<*>>>(path).toTypedArray()
        else -> throw Config4kException.UnSupportedType(clazz)
    }
})
