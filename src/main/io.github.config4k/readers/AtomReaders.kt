package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.Config4kException
import io.github.config4k.extract
import java.time.Duration
import kotlin.reflect.KClass

internal sealed class AtomReader<out T> : BaseReader<T>() {
    companion object {
        fun select(mapperClass: KClass<*>) =
                when (mapperClass) {
                    Int::class -> IntReader
                    String::class -> StringReader
                    Boolean::class -> BooleanReader
                    Double::class -> DoubleReader
                    Long::class -> LongReader
                    Duration::class -> DurationReader
                    else ->
                        if (mapperClass.java.isEnum) EnumReader
                        else null
                }
    }
    protected abstract fun readValue(config: Config , key: String): T
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): T = readValue(config, path)
}

internal object BooleanReader : AtomReader<Boolean>() {
    override fun readValue(config: Config, key: String): Boolean = config.getBoolean(key)
}

internal object DoubleReader : AtomReader<Double>() {
    override fun readValue(config: Config, key: String): Double = config.getDouble(key)
}

internal object DurationReader : AtomReader<Duration>() {
    override fun readValue(config: Config, key: String): Duration = config.getDuration(key)
}

internal object IntReader : AtomReader<Int>() {
    override fun readValue(config: Config, key: String): Int = config.getInt(key)
}

internal object LongReader : AtomReader<Long>() {
    override fun readValue(config: Config, key: String): Long = config.getLong(key)
}

internal object StringReader : AtomReader<String>() {
    override fun readValue(config: Config, key: String): String = config.getString(key)
}

internal object EnumReader: AtomReader<Enum<*>>() {
    override fun readValue(config: Config, key: String): Enum<*> {
        throw UnsupportedOperationException("use readInternal")
    }

    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Enum<*> {
        val enumName = config.extract<String>(path)
        val enumConstants = clazz.mapperClass.java.enumConstants
        return enumConstants.find { it.toString() == enumName } as? Enum<*>
                ?: throw Config4kException
                        .WrongEnum(enumConstants.map(Any::toString), enumName)
    }
}