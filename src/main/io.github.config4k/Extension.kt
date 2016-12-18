package io.github.config4k

import com.typesafe.config.Config
import io.github.config4k.readers.SelectReader

/**
 * An extension function that enables you to use type parameter.
 *
 * Returns a value of given type by calling method
 * in [com.typesafe.config.Config]
 *
 * As this function is an inline function, shown stacktrace is not true.
 *
 * @param path see [com.typesafe.config.Config]
 */
inline fun <reified T> Config.extract(path: String) =
        SelectReader.getReader(T::class)(this, path) as T
