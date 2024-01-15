package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.github.config4k.readers.SelectReader
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.time.Duration
import java.util.UUID
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

/**
 * An extension function that enables you to use type parameter.
 *
 * Returns a value of given type by calling method
 * in [com.typesafe.config.Config]
 *
 * As this function is an inline function, shown stacktrace is not true.
 *
 * @param path see [com.typesafe.config.Config]
 * @param defaultValue will be return if Config doesn't contain value by path
 */
public inline fun <reified T> Config.extract(
    path: String,
    defaultValue: T? = null,
): T {
    val genericType = object : TypeReference<T>() {}.genericType()

    val result =
        SelectReader.getReader(ClassContainer(T::class, genericType))(this, path) ?: defaultValue

    return try {
        result as T
    } catch (e: Exception) {
        throw result?.let { e } ?: ConfigException.BadPath(
            path,
            "take a look at your config",
        )
    }
}

/**
 * Loads whole config into one data class.
 */
public inline fun <reified T> Config.extract(): T {
    val genericType = object : TypeReference<T>() {}.genericType()

    val result = SelectReader.extractWithoutPath(ClassContainer(T::class, genericType), this)

    return try {
        result as T
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @param thisRef
 *            the owner of the property
 * @param property
 *            the property to populate
 * @return the configured value converted to the property's type
 */
public inline operator fun <R, reified T> Config.getValue(
    thisRef: R,
    property: KProperty<*>,
): T {
    val genericType = object : TypeReference<T>() {}.genericType()
    val clazz = ClassContainer(T::class, genericType)
    val reader = SelectReader.getReader(clazz)
    val path = property.name
    val result = reader(this, path)
    return try {
        result as T
    } catch (e: Exception) {
        throw result
            ?.let { e }
            ?: ConfigException.BadPath(path, "take a look at your config")
    }
}

/**
 * Converts the receiver object to Config.
 *
 * @param name the returned config's name
 */
public fun Any.toConfig(name: String): Config {
    val clazz = this.javaClass.kotlin
    for (customType in customTypeRegistry) {
        if (customType.testToConfig(this)) {
            return customType.toConfig(this, name)
        }
    }
    val map =
        when {
            clazz.javaPrimitiveType != null -> mapOf(name to this)
            this is String -> mapOf(name to this)
            this is Enum<*> -> mapOf(name to this.name)
            this is File -> mapOf(name to this.toString())
            this is Path -> mapOf(name to this.toString())
            this is UUID -> mapOf(name to this.toString())
            this is URL -> mapOf(name to this.toString())
            this is Duration -> mapOf(name to if (this.nano == 0) "${this.seconds} s" else "${this.toNanos()} ns")
            this is Iterable<*> -> {
                val list =
                    this.map {
                        it?.toConfigValue()?.unwrapped()
                    }
                mapOf(name to list)
            }

            this is Map<*, *> -> {
                val stringKeys = this.keys.all { it is String }
                if (stringKeys) {
                    val map =
                        this
                            .mapKeys { "\"${it.key}\"" }
                            .mapValues { it.value?.toConfigValue()?.unwrapped() }
                    mapOf(name to map)
                } else {
                    val list =
                        this.map { (key, value) ->
                            MapEntry(key, value).toConfigValue()
                        }
                    mapOf(name to list)
                }
            }

            clazz.primaryConstructor != null ->
                mapOf(name to getConfigMap(this, clazz))

            clazz.objectInstance != null -> mapOf(name to emptyMap<String, Any>())
            else -> throw Config4kException.UnSupportedType(clazz)
        }

    return ConfigFactory.parseMap(map)
}
