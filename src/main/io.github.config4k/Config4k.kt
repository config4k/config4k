@file:JvmName("Config4k")
package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueType
import io.github.config4k.readers.Readers
import java.util.*
import kotlin.reflect.full.primaryConstructor

/**
 * An extract function that does not require a starting path -- i.e., it attempts to map from the root of the object.
 */
inline fun <reified T> Config.extract(): T = doExtract("", true)

/**
 * Java interop variant of the [Config.extract()] function.
 */
fun <T: Any> Config.extract(clazz: Class<T>): T =
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
fun <T: Any> Config.extract(clazz: Class<T>, path: String): T =
        clazz.cast(ClassContainer(clazz.kotlin).let { Readers.select(it).read(it, this, path, false) })


@PublishedApi
internal inline fun <reified T> Config.doExtract(path: String, permitEmptyPath: Boolean= false): T {
    val clazz = ClassContainer(T::class, object : TypeReference<T>() {}.genericType())
    val result = Readers.select(clazz).read(clazz, this, path, permitEmptyPath)

    return try {
        result as T
    } catch (e: Exception) {
        throw result?.let { e } ?: ConfigException.BadPath(
                path, "take a look at your config")
    }
}

/**
 * Get config using the package name of a class.
 *
 * @param T The class to use the package name from.
 *
 */
inline fun <reified T> Config.forPackageOf(): Config =
    getConfig(T::class.java.`package`.name)

/**
 * Map the config object to a [java.util.Properties] object. The passed in [Config] should only contain atoms  --i.e., not [ConfigValueType.OBJECT] or
 * [ConfigValueType.LIST]. [ConfigValueType.NULL] uses zero-value semantics --i.e., it is mapped to the empty string.
 */
fun Config.toProperties(): Properties =
        Properties().also { props ->
            this.root().forEach { prop ->
                when(prop.value.valueType()) {
                    ConfigValueType.LIST, ConfigValueType.OBJECT  ->
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
                (it.key as? String) ?:
                        throw Config4kException.UnSupportedType(clazz)
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
