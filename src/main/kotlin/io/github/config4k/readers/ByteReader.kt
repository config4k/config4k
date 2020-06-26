package io.github.config4k.readers

internal class ByteReader : Reader<Byte>({ config, path -> config.getInt(path).toByte() })
