package io.github.config4k

import javax.inject.Qualifier
import kotlin.reflect.KClass


/**
 * Applies to binding situations where there will be multiple Config objects bound. This qualifier denotes the
 * [com.typesafe.config.Config] for the application.
 *
 * @param namespace an optional namespace, the path to the application configuration. Note: currently only used for
 * informational purposes.
 */
@Qualifier
annotation class Application(val namespace: String = "")

/**
 * This changes the namespace of the bean object. To change the path completely use the class[clazz] or the
 * [path] field. This annotation may also be used just to add to the package namespace -- .
 *
 * @param clazz Another clazz to inherit the path from.
 * @param path A path to use instead of inferring it.
 * @param key A key to use in addition to the path. This is useful when multiple services exist within the same package.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Namespace(val path: String="", val clazz: KClass<out Any> = Nothing::class, val key: Key = Key(""))

/**
 * If used on a property it changes the name of the property.
 *
 * If used on  a containing class it adds this to the package name. This is so that the same package can hold
 * configuration for multiple services.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Key(val path: String)