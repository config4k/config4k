package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import java.net.URI
import java.net.URL

/**
 * Serializer for [URL]
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleURL(
 *   @Serializable(URLSerializer::class)
 *   val url: URL,
 * )
 * val config = ConfigFactory.parseString("url = "https://www.example.com"")
 * val exampleURL: ExampleURL = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleURL.serializer(), exampleURL)
 * ```
 */
public object URLSerializer :
    KSerializer<URL> by stringSerializer("hocon.${URL::class.qualifiedName}", { URI(it).toURL() }, URL::toExternalForm)
