package io.github.config4k.readers

import com.typesafe.config.Config

internal class IntReader : Reader<Int>(Config::getInt)
