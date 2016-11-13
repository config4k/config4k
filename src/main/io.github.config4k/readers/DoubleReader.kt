package io.github.config4k.readers

import com.typesafe.config.Config


internal class DoubleReader : Reader<Double>(Config::getDouble)