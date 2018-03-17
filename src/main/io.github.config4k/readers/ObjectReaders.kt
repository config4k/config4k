package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import io.github.config4k.ClassContainer
import io.github.config4k.extract
import io.github.config4k.getGenericList
import io.github.config4k.toProperties
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType


internal sealed class ObjectReader<out T>: BaseReader<T>() {
    protected fun selectConfig(config: Config, path: String, permitEmpty: Boolean): Config =
            if (permitEmpty && path.isEmpty()) {
                config
            } else config.extract(path)

    companion object {
        fun select(clazz: ClassContainer) =
                when (clazz.mapperClass) {
                    Config::class -> ConfigReader
                    ConfigValue::class -> ConfigValueReader
                    Properties::class -> PropertiesReader
                    Map::class -> MapReader
                    else -> when {
                        clazz.mapperClass.primaryConstructor != null -> ArbitraryTypeReader
                        clazz.mapperClass.objectInstance != null -> AnyObjectReader
                        else -> null
                    }
                }
    }
}

internal object ConfigReader : ObjectReader<Config>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Config = config.getConfig(path)!!
}

internal object ConfigValueReader: ObjectReader<ConfigValue>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): ConfigValue = config.getValue(path)!!
}


internal object MapReader : ObjectReader<Map<String, *>>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Map<String, *> {
        return config.getConfig(path).let { child ->
            child.root().entries.map {
                it.key to Readers.select(clazz.typeArguments[1]).read(clazz.typeArguments[1], child, it.key)
            }.toMap()
        }
    }
}

internal object AnyObjectReader : ObjectReader<Any>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Any = clazz.mapperClass.objectInstance!!
}

internal object PropertiesReader : ObjectReader<Properties>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Properties =
            selectConfig(config, path, permitEmptyPath).toProperties()
}


internal object ArbitraryTypeReader : ObjectReader<Any>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Any {
        var typeArgumentIndex = 0
        val constructor = clazz.mapperClass.primaryConstructor!!

        val parameters = constructor.parameters.map {
            val type = it.type.javaType
            val paramClazz = (type as? ParameterizedType)?.let {
                ClassContainer((it.rawType as Class<*>).kotlin, getGenericList(it))
            } ?: (type as? Class<*>)?.let {
                ClassContainer(it.kotlin)
            } ?: clazz.typeArguments[typeArgumentIndex++]
            val reader = Readers.select(paramClazz)
            val resolvedConfig = selectConfig(config, path, permitEmptyPath)
            it to reader.read(paramClazz, resolvedConfig, it.name!!)
        }.toMap()

        return constructor.callBy(parameters)
    }
}