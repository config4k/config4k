package io.github.config4k.readers

import com.typesafe.config.Config

internal class LongReader : Reader<Long>(Config::getLong)
