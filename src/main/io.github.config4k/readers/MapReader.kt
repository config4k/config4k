package io.github.config4k.readers

import io.github.config4k.ClassContainer
import io.github.config4k.MapEntry

internal class MapReader(keyClass: ClassContainer, valueClass: ClassContainer) : Reader<Map<*, *>?>({
    config, path ->
    when(keyClass.mapperClass){
        String::class ->{
            val child = config.getConfig(path)
            child.root().keys.associate{ key ->
                key to SelectReader.getReader(valueClass)(child, key)
            }
        }
        else -> {
            val mapEntryClassContainer = ClassContainer(MapEntry::class, mapOf("K" to keyClass, "V" to valueClass))
            ListReader(mapEntryClassContainer).getValue(config, path)?.associate {
                val mapEntry = it as MapEntry<*,*>
                mapEntry.key to mapEntry.value
            }
        }
    }
})
