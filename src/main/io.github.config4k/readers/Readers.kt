package io.github.config4k.readers

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.Config4kException
import io.github.config4k.Reader


object Readers {
    /**
     * @param clazz a instance got from the given type by reflection
     * @throws Config4kException.UnSupportedType if the passed type is not supported
     */
    fun select(clazz: ClassContainer): Reader<*> =
            AtomReader.select(clazz.mapperClass) ?: EnumerableReader.select(clazz) ?: ObjectReader.select(clazz)
            ?: throw Config4kException.UnSupportedType(clazz.mapperClass)
}

internal abstract class BaseReader<out T> : Reader<T> {
    // TODO remove the need for this method and just use readInternal. deal with absent paths elsewhere.
    final override fun read(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean): T? =
            if (permitEmptyPath || config.hasPath(path))
                readInternal(clazz, config, path, permitEmptyPath)
            else null


    internal abstract fun readInternal(clazz: ClassContainer, config: Config, path: String, permitEmptyPath: Boolean = false): T
}

