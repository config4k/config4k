package io.github.config4k.readers

import com.typesafe.config.Config

/**
 * Don't implement this class.
 * Same as [io.github.config4k.readers.SelectReader]
 *
 * To support new type, implement this class.
 *
 * @param T support type
 */
internal open class Reader<out T>(val read: (Config, String) -> T)