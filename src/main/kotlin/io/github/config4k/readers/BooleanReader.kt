package io.github.config4k.readers

import com.typesafe.config.Config

internal class BooleanReader : Reader<Boolean>(Config::getBoolean)
