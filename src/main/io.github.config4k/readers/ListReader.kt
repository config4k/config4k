package io.github.config4k.readers


internal class ListReader : Reader<List<*>>({ config, path -> config.getList(path).unwrapped() })
