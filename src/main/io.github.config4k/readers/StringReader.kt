package io.github.config4k.readers

import com.typesafe.config.Config


internal class StringReader : Reader<String>(Config::getString)