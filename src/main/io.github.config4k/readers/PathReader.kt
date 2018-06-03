package io.github.config4k.readers

import java.nio.file.Path
import java.nio.file.Paths

internal class PathReader: Reader<Path>({config, path -> Paths.get(config.getString(path))})
