package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class ListReader(clazz: List<ClassContainer>) : Reader<List<*>>({
    config, path ->
    val reader = SelectReader.getReader(clazz[0])
    config.getList(path).map {
        val dummyName = "key"
        reader(it.atKey(dummyName), dummyName)
    }
})
