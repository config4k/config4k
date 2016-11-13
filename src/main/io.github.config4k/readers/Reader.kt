package io.github.config4k.readers

import com.typesafe.config.Config


open class Reader<out T>(val read: (Config, String) -> T)