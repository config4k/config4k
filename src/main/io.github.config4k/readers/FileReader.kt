package io.github.config4k.readers

import java.io.File

internal class FileReader: Reader<File>({config, path -> File(config.getString(path)) })
