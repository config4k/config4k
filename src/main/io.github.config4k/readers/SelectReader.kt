package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import io.github.config4k.Config4kException
import java.time.Duration
import kotlin.reflect.KClass


object SelectReader {
    /**
     * Don't use this method.
     * Called by an inline function [io.github.config4k.Extension.extract],
     * this method is public even though it is just for internal.
     *
     * Add new case to support new type.
     *
     * @param clazz a instance got from the given type by reflection
     * @param genericType clazz is C and genericType is A,
     *                    when the passed type is C<A>
     * @throws Config4kException.UnSupportedType if the passed type is not supported
     */
    fun getReader(clazz: KClass<*>,
                  genericType: KClass<*>?): (Config, String) -> Any =
            when (clazz) {
                Int::class -> IntReader()
                String::class -> StringReader()
                Boolean::class -> BooleanReader()
                Double::class -> DoubleReader()
                Long::class -> LongReader()
                Duration::class -> DurationReader()
                Config::class -> ConfigReader()
                ConfigValue::class -> ConfigValueReader()
                List::class -> ListReader(genericType!!)
                else -> throw Config4kException.UnSupportedType(clazz)
            }.read
}