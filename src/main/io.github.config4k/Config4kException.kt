package io.github.config4k

import com.typesafe.config.ConfigException
import kotlin.reflect.KClass


open class Config4kException @PublishedApi internal constructor(
    message: String,
    throwable: Throwable? = null
) : ConfigException(message, throwable) {

    class UnSupportedType(
        type: KClass<*>
    ) : Config4kException("type: ${type.qualifiedName} is not supported")

    class WrongEnum(
        enumConstants: List<String>,
        actualValue: String
    ) : Config4kException("expected : $enumConstants, actually : $actualValue")

    class InvalidShape(
        message: String
    ) : Config4kException(message)
}