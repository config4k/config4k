package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigMemorySize

internal class MemorySizeReader : Reader<ConfigMemorySize>(Config::getMemorySize)
