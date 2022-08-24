package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class SetReader(clazz: ClassContainer, mutable: Boolean = false) : Reader<Set<*>?>({ config, path ->
    ListReader(clazz).getValue(config, path)?.let { if (mutable) it.toMutableSet() else it.toSet() }
})
