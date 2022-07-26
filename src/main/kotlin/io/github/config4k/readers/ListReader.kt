package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class ListReader(clazz: ClassContainer) : Reader<List<*>>({ config, path ->
    val reader = SelectReader.getReader(clazz)
    config.getList(path).map {
        val dummyName = "key"
        reader(it.atKey(dummyName), dummyName)
    }
})
