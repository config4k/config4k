package io.github.config4k.readers

import com.typesafe.config.Config

internal class ConfigReader : Reader<Config>(Config::getConfig)
