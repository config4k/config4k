package io.github.config4k.readers

import kotlin.reflect.KClass

internal class ListReader(clazz: List<KClass<*>>) : Reader<List<*>>({
    config, path ->
    val reader = SelectReader.getReader(clazz.drop(1))
    config.getList(path).map {
        val dummyName = "key"
        reader(it.atKey(dummyName), dummyName)
    }
})
