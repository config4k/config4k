package io.github.config4k.readers

import io.github.config4k.toProperties
import java.util.*


internal class PropertiesReader(override val permitEmptyPath: Boolean = false) : Reader<Properties>({ config, path ->
    selectConfig(config, path, permitEmptyPath).toProperties()
})