package io.github.config4k.readers

import kotlin.reflect.KClass


internal class MapReader(clazz: List<KClass<*>>) : Reader<Map<String, *>>({
    config, path ->
    val child = config.getConfig(path)
    child.root().entries.map {
        val key = it.key
        key to SelectReader.getReader(clazz.drop(1))(child, key)
    }.toMap()
})