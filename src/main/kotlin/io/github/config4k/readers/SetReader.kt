package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class SetReader(clazz: ClassContainer) : Reader<Set<*>?>({ config, path ->
    ListReader(clazz).getValue(config, path)?.toSet()
})
