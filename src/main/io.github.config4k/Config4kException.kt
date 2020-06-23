package io.github.config4k

import kotlin.reflect.KClass

class Config4kException {
    class UnSupportedType(type: KClass<*>) :
        RuntimeException("type: ${type.qualifiedName} is not supported")

    class WrongEnum(enumConstants: List<String>, actualValue: String) :
        RuntimeException("expected : $enumConstants, actually : $actualValue")
}
