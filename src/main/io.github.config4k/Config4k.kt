@file:JvmName("Config4k")

package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueType
import io.github.config4k.readers.Readers
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

/**
 * An extract function that does not require a starting path -- i.e., it attempts to map from the root of the object.
 */
inline fun <reified T> Config.extract(): T = doExtract("", true)

/**
 * Java interop variant of the [Config.extract()] function.
 */
fun <T : Any> Config.extract(clazz: Class<T>): T =
    clazz.cast(ClassContainer(clazz.kotlin).let { Readers.select(it).read(it, this, "", true) })

/**
 * Map [Config] to Kotlin types.
 *
 * @param path the config destructuring begins at this path
 */
inline fun <reified T> Config.extract(path: String): T = require(path.isNotEmpty()).let { doExtract(path) }

/**
 * Java interop variant of the [Config.extract()] function.
 *
 * @param path the config destructuring begins at this path
 */
fun <T : Any> Config.extract(clazz: Class<T>, path: String): T {
    val container = ClassContainer(clazz.kotlin)
    val reader = Readers.select(container)
    val result = reader.read(container, this, path, false) ?: throw Config4kException("path $path not present")
    @Suppress("UNCHECKED_CAST")
    return if (clazz.isPrimitive) {
        result as T
    } else {
        clazz.cast(result)
    }
}


@PublishedApi
internal inline fun <reified T> Config.doExtract(path: String, permitEmptyPath: Boolean = false): T {
    val clazz = ClassContainer(T::class, object : TypeReference<T>() {}.genericType())
    val result = Readers.select(clazz).read(clazz, this, path, permitEmptyPath)

    return try {
        result as T
    } catch (e: Exception) {
        throw result?.let { e } ?: ConfigException.BadPath(
            path, "take a look at your config"
        )
    }
}

/**
 * Get config using the package name of a class.
 *
 * @param T The class to use the package name from.
 * @see com.typesafe.config.Config.getConfig
 */
inline fun <reified T> Config.forPackageOf(): Config =
    T::class.java.`package`.let { pkg ->
        try {
            getConfig(T::class.java.`package`.name)
        } catch (ex: ConfigException) {
            throw Config4kException("could not get config for class: ${T::class.java.simpleName} at path: $pkg")
        }
    }

/**
 * Extract a class by the package it's defined in and an optional sub-path.
 */
inline fun <reified T> Config.extractByPackage(path: String = ""): T =
    forPackageOf<T>().let {
        try {
            it.doExtract(path, true)
        } catch (ex: ConfigException) {
            throw Config4kException("could not extract config from path: ${T::class.java.`package`}.$path", ex)

        }
    }

/**
 * Map the config object to a [java.util.Properties] object. The passed in [Config] should only contain atoms  --i.e., not [ConfigValueType.OBJECT] or
 * [ConfigValueType.LIST]. [ConfigValueType.NULL] uses zero-value semantics --i.e., it is mapped to the empty string.
 */
fun Config.toProperties(): Properties =
    Properties().also { props ->
        this.root().forEach { prop ->
            when (prop.value.valueType()) {
                ConfigValueType.LIST, ConfigValueType.OBJECT ->
                    throw Config4kException.InvalidShape("cannot render Properties as the Config key ${prop.key} is of type ${prop.value.valueType()}")
                else ->
                    props.setProperty(prop.key, if (this.getIsNull(prop.key)) "" else this.getString(prop.key))
            }
        }
    }

/**
 * Converts the receiver object to Config.
 *
 * @param name the returned config's name
 */
fun Any.toConfig(name: String): Config {
    val clazz = this.javaClass.kotlin
    val map = when {
        clazz.javaPrimitiveType != null -> mapOf(name to this)
        this is String -> mapOf(name to this)
        this is Enum<*> -> mapOf(name to this.name)
        this is Iterable<*> -> {
            val list = this.map {
                it?.toConfigValue()?.unwrapped()
            }
            mapOf(name to list)
        }
        this is Map<*, *> -> {
            val map = this.mapKeys {
                (it.key as? String) ?: throw Config4kException.UnSupportedType(clazz)
            }.mapValues {
                it.value?.toConfigValue()?.unwrapped()
            }
            mapOf(name to map)
        }
        clazz.primaryConstructor != null ->
            mapOf(name to getConfigMap(this, clazz))
        clazz.objectInstance != null -> mapOf(name to emptyMap<String, Any>())
        else -> throw Config4kException.UnSupportedType(clazz)
    }

    return ConfigFactory.parseMap(map)
}

/**
 * Shorthand for [extract].
 */
inline operator fun <reified T> Config.get(key: String): T = this.extract(key)

/**
 * A scheme for concisely binding config in a class constructor in an immutable manner. These scheme is intended to be
 * used to read a single objects configuration properties out.
 *
 * The attribute name is used verbatim to extract the config, otherwise if the [Key] annotation is set on the
 * attribute it changes the attribute name. The key can be a path.
 *
 * The namespace of the containing class is used for the root of the config. The [Namespace] annotation can be used to ch
 *
 * @param property The member property we are trying to extract from config.
 */
inline operator fun <reified T> Config.get(property: KProperty<T>): T {
    check(property.isFinal) { "$property must be final" }

    val declaringClass = checkNotNull(property.javaField) { "must be a field" }.declaringClass

    val namespace = declaringClass.annotations.filterIsInstance<Namespace>().firstOrNull().let {
        if (it != null) {
            check(it.path.isNotBlank() || it.clazz != Nothing::class || it.key.path.isNotBlank()) {
                "@Namespace annotation used but it is not setting a namespace or contributing a key element"
            }
            check(!(it.path.isNotBlank() && it.clazz != Nothing::class)) {
                "can only pick a namespace from a class or override it with a new path"
            }
            val ns = when {
                it.path.isNotBlank() -> it.path
                it.clazz != Nothing::class -> it.clazz.java.`package`.name
                else -> declaringClass.`package`.name
            }
            when {
                it.key.path.isNotBlank() -> "$ns.${it.key.path}"
                else -> ns
            }
        } else {
            declaringClass.`package`.name
        }
    }


    val propName = property.annotations.filterIsInstance<Key>().firstOrNull()?.path?.also {
        check(it.isNotBlank()) { "${property.name} has a key annotation with no value" }
    } ?: property.name

    return this.getConfig(namespace).extract(propName)
}