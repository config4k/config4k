package io.github.config4k.readers

internal class FloatReader : Reader<Float>({ config, key -> config.getDouble(key).toFloat() })
