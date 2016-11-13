package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue


internal class ConfigValueReader : Reader<ConfigValue>(Config::getValue)