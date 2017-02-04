package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
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
inline fun <reified T> Config.extract(path: String): T {
    val genericType = object : TypeReference<T>() {}.genericType()
    val clazz = listOf(T::class)
    val result = SelectReader.getReader(
            genericType?.let { clazz + it } ?: clazz)(this, path)
    return try {
        result as T
    } catch (e: Exception) {
        throw result?.let { e } ?: ConfigException.BadPath(
                path, "take a look at your config")
    }
}
