package io.github.config4k.readers

import io.github.config4k.ClassContainer


internal class MapReader(clazz: ClassContainer) : Reader<Map<String, *>>({
    config, path ->
    val child = config.getConfig(path)
    child.root().entries.map {
        val key = it.key
        key to SelectReader.getReader(clazz)(child, key)
    }.toMap()
})
