package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.Config4kException
import io.github.config4k.extract
import java.time.Duration

internal sealed class EnumerableReader<out T> : BaseReader<T>() {
    companion object {
        fun select(clazz: ClassContainer) =
                when (clazz.mapperClass) {
                    List::class -> ListReader
                    Set::class -> SetReader
                    else -> when {
                        clazz.mapperClass.java.isArray -> ArrayReader
                        else -> null
                    }
                }
    }
}

internal object ListReader : EnumerableReader<List<*>>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): List<*> {
        val itemClazz = clazz.typeArguments[0]
        val reader = Readers.select(itemClazz)
        return config.getList(path).map {
            val dummyName = "key"
            reader.read(itemClazz, it.atKey(dummyName), dummyName)
        }
    }
}

internal object ArrayReader : EnumerableReader<Array<*>>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Array<*> =
            clazz.mapperClass.java.componentType.kotlin.let {
                when(it) {
                    Int::class -> config.extract<List<Int>>(path).toTypedArray()
                    String::class -> config.extract<List<String>>(path).toTypedArray()
                    Boolean::class -> config.extract<List<Boolean>>(path).toTypedArray()
                    Double::class -> config.extract<List<Double>>(path).toTypedArray()
                    Long::class -> config.extract<List<Long>>(path).toTypedArray()
                    Duration::class -> config.extract<List<Duration>>(path).toTypedArray()
                    Config::class -> config.extract<List<Config>>(path).toTypedArray()
                    List::class -> config.extract<List<List<*>>>(path).toTypedArray()
                    Set::class -> config.extract<List<Set<*>>>(path).toTypedArray()
                    else -> throw Config4kException.UnSupportedType(it)
                }
            }
}

internal object SetReader : EnumerableReader<Set<*>?>() {
    override fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): Set<*>? = ListReader.readInternal(clazz, config, path).toSet()
}