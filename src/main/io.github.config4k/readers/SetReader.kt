package io.github.config4k.readers

import kotlin.reflect.KClass


internal class SetReader(clazz: List<KClass<*>>) : Reader<Set<*>>({
    config, path ->
    ListReader(clazz).read(config, path).toSet()
})