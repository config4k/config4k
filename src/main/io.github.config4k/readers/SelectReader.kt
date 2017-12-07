package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import io.github.config4k.Config4kException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


object SelectReader {
    /**
     * Don't use this method.
     * Called by an inline function [io.github.config4k.Extension.extract],
     * this method is public even though it is just for internal.
     *
     * Add new case to support new type.
     *
     * @param clazz a instance got from the given type by reflection
     * @throws Config4kException.UnSupportedType if the passed type is not supported
     */
    fun getReader(clazz: List<KClass<*>>) =
            when (clazz[0]) {
                Int::class -> IntReader()
                String::class -> StringReader()
                Boolean::class -> BooleanReader()
                Double::class -> DoubleReader()
                Long::class -> LongReader()
                Config::class -> ConfigReader()
                ConfigValue::class -> ConfigValueReader()
                List::class -> ListReader(clazz)
                Set::class -> SetReader(clazz)
                Map::class -> MapReader(clazz)
                else ->
                    when {
                        clazz[0].java.isArray ->
                            ArrayReader(clazz[0].java.componentType.kotlin)
                        clazz[0].java.isEnum -> EnumReader(clazz[0])
                        else -> clazz[0].primaryConstructor?.let {
                            ArbitraryTypeReader(clazz[0])
                        } ?: throw Config4kException.UnSupportedType(clazz[0])
                    }
            }.getValue
}
