package io.github.config4k

import com.typesafe.config.Config

interface Reader <out T> {
    fun read(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean = false): T?
}
