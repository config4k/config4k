package io.github.config4k.readers

import kotlin.reflect.KClass


internal class ListReader(type: KClass<*>) : Reader<List<*>>({
    config, path ->
    val configList = config.getList(path)
    configList.map {
        val dummyName = "key"
        SelectReader.getReader(type, null)(it.atKey(dummyName), dummyName)
    }
})