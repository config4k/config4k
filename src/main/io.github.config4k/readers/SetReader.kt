package io.github.config4k.readers


internal class SetReader : Reader<Set<*>>({
    config, path ->
    ListReader().read(config, path).toSet()
})