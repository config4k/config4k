package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class ListReader(clazz: ClassContainer, mutable: Boolean = false) : Reader<List<*>>({ config, path ->
    val reader = SelectReader.getReader(clazz)
    config.getList(path).map {
        val dummyName = "key"
        reader(it.atKey(dummyName), dummyName)
    }.let { if (mutable) it.toMutableList() else it }
})
