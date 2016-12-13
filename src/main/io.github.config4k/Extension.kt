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
inline fun <reified T : Any> Config.extract(path: String): T {
    val genericType = object : TypeReference<T>() {}.genericType()
    return (SelectReader.getReader(T::class, genericType))(this, path) as T
}