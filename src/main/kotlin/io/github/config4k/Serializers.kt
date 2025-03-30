package io.github.config4k

import io.github.config4k.serializers.JavaBeanSerializer

/**
 * Creates a [JavaBeanSerializer] for a type is specified as Java Bean.
 * @return [JavaBeanSerializer]
 */
public inline fun <reified T> javaBeanSerializer(): JavaBeanSerializer<T> = JavaBeanSerializer(T::class.java)
