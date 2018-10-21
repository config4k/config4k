package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigMemorySize
import com.typesafe.config.ConfigValue
import io.github.config4k.ClassContainer
import io.github.config4k.Config4kException
import io.github.config4k.customTypeRegistry
import java.io.File
import java.nio.file.Path
import java.time.Duration
import java.time.Period
import java.time.temporal.TemporalAmount
import kotlin.reflect.full.primaryConstructor

/**
 * Don't use this class.
 * Called by an inline function [io.github.config4k.Extension],
 * this class is public even though it is just for internal.
 */
object SelectReader {
    /**
     * Add new case to support new type.
     *
     * @param clazz a instance got from the given type by reflection
     * @throws Config4kException.UnSupportedType if the passed type is not supported
     */
    fun getReader(clazz: ClassContainer): (Config, String)->Any? {
        for (customType in customTypeRegistry) {
            if (customType.testParse(clazz)) {
                return Reader { config, name -> customType.parse(clazz, config, name) }.getValue
            }
        }
        return when (clazz.mapperClass) {
            Int::class -> IntReader()
            String::class -> StringReader()
            Boolean::class -> BooleanReader()
            Byte::class -> ByteReader()
            Double::class -> DoubleReader()
            Long::class -> LongReader()
            Duration::class -> DurationReader()
            Period::class -> PeriodReader()
            TemporalAmount::class -> TemporalAmountReader()
            ConfigMemorySize::class -> MemorySizeReader()
            Config::class -> ConfigReader()
            ConfigValue::class -> ConfigValueReader()
            List::class -> ListReader(clazz.typeArguments.getValue("E"))
            Set::class -> SetReader(clazz.typeArguments.getValue("E"))
            Map::class -> MapReader(clazz.typeArguments.getValue("K"), clazz.typeArguments.getValue("V"))
            File::class -> FileReader()
            Path::class -> PathReader()
            Regex::class -> RegexReader()
            else ->
                when {
                    clazz.mapperClass.java.isArray ->
                        ArrayReader(clazz.mapperClass.java.componentType.kotlin)
                    clazz.mapperClass.java.isEnum -> EnumReader(clazz.mapperClass)
                    clazz.mapperClass.primaryConstructor != null -> ArbitraryTypeReader(clazz)
                    clazz.mapperClass.objectInstance != null -> ObjectReader(clazz)
                    else -> throw Config4kException.UnSupportedType(clazz.mapperClass)
                }
        }.getValue
    }

    fun extractWithoutPath(clazz: ClassContainer, config: Config) =
            extractWithParameters(clazz, config)
}
