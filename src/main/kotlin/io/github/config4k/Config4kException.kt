package io.github.config4k

import kotlin.reflect.KClass

public class Config4kException {
    public class UnSupportedType(
        type: KClass<*>,
    ) : RuntimeException("type: ${type.qualifiedName} is not supported")

    public class WrongEnum(
        enumConstants: List<String>,
        actualValue: String,
    ) : RuntimeException("expected : $enumConstants, actually : $actualValue")
}
