package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.junit.Assert
import org.opentest4j.AssertionFailedError

internal inline fun <reified T> Config.assertEqualsAtPath(path: String, expected: T) {
    extract<T>(path).also {
        Assert.assertEquals(it, expected)
    }
}

internal inline fun <reified T> Config.assertEqualsAfterRepositioning(path: String, expected: T) {
    getConfig(path).extract<T>().also {
        Assert.assertEquals(it, expected)
    }
}

internal fun withConfig(config: String, block: Config.() -> Unit) = ConfigFactory.parseString(config).block()

inline fun <reified T> shouldThrow(block: () -> Unit): Exception {
    try {
        block()
        throw AssertionFailedError("expected exception of type ${T::class.qualifiedName} to be thrown")
    } catch (ex: Exception) {
        if (ex.javaClass !== T::class.java) {
            throw AssertionFailedError("expected exception of type ${T::class.qualifiedName} to be thrown got ${ex.javaClass.canonicalName}")
        } else {
            return ex
        }
    }
}

internal inline infix fun <reified T> T.shouldBe(expected: T) { Assert.assertEquals(expected, this) }
//inline infix fun <reified T> Comparable<T>.shouldBe(expected: T) { Assert.assertEquals(expected, this) }
//inline infix fun <reified T, L: List<T>> L.shouldBe(expected: T) { Assert.assertEquals(expected, this) }
//inline infix fun <K,V, reified M: Map<K,V>> M.shouldBe(expected: M) { Assert.assertEquals(expected, this) }