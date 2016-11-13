package io.github.config4k

import com.typesafe.config.Config
import io.github.config4k.readers.SelectReader

inline fun <reified T> Config.extract(path: String): T = run {
    val reader = SelectReader().getReader(T::class)
    reader.read(this, path) as T
}
