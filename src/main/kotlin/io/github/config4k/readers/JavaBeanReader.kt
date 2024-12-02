package io.github.config4k.readers

import com.typesafe.config.ConfigBeanFactory
import kotlin.reflect.KClass

internal class JavaBeanReader(
    clazz: KClass<*>,
) : Reader<Any?>({ config, path ->
        ConfigBeanFactory.create(config.getConfig(path), clazz.java)
    })
