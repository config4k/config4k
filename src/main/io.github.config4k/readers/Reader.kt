package io.github.config4k.readers

import com.google.common.base.CaseFormat.LOWER_HYPHEN
import com.google.common.base.CaseFormat.LOWER_CAMEL
import com.typesafe.config.Config

/**
 * Don't implement this class.
 * Same as [io.github.config4k.readers.SelectReader]
 *
 * To support new type, implement this class.
 *
 * @param T support type
 */
internal open class Reader<out T>(read: (Config, String) -> T) {
    val getValue: (Config, String) -> T? = value@{
        config, path ->
        if (config.hasPath(path)) return@value read(config, path)
        /*
        If path not present, try converting it to hyphenated case and try again. This is the
        preferred format
        for HOCON files.
         */
        val hyphenPath = camelCaseToLowerHyphenCase(path)
        if (config.hasPath(hyphenPath)) read(config, hyphenPath) else null
    }

    companion object {
        // see https://stackoverflow.com/questions/2559759
        private fun camelCaseToLowerHyphenCase(camelCase: String): String =
            "(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])"
                .toRegex()
                .replace(camelCase, "-")
                .toLowerCase()
    }
}