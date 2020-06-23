package io.github.config4k.readers

internal class RegexReader : Reader<Regex>({ config, path -> config.getString(path).toRegex() })
