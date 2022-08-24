package io.github.config4k.readers

import com.typesafe.config.ConfigUtil
import io.github.config4k.ClassContainer
import io.github.config4k.MapEntry

internal class MapReader(keyClass: ClassContainer, valueClass: ClassContainer, mutable: Boolean = false) : Reader<Map<*, *>?>({ config, path ->
    when (keyClass.mapperClass) {
        String::class -> {
            val child = config.getConfig(path)
            child.root().keys.associateWith { key -> SelectReader.getReader(valueClass)(child, ConfigUtil.joinPath(key)) }
        }
        else -> {
            val mapEntryClassContainer = ClassContainer(MapEntry::class, mapOf("K" to keyClass, "V" to valueClass))
            ListReader(mapEntryClassContainer).getValue(config, path)?.associate {
                val mapEntry = it as MapEntry<*, *>
                mapEntry.key to mapEntry.value
            }
        }
    }.let { if (mutable) it?.toMutableMap() else it }
})
