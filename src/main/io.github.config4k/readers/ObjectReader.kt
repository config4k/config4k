package io.github.config4k.readers

import io.github.config4k.ClassContainer

internal class ObjectReader(clazz: ClassContainer) : Reader<Any>({
    _, _ ->
    clazz.mapperClass.objectInstance!!
})