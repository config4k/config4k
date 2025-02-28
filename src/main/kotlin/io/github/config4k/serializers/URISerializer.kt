package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import java.net.URI

/**
 * Serializer for [URI]
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleURI(
 *   @Serializable(URISerializer::class)
 *   val uri: URI,
 * )
 * val config = ConfigFactory.parseString("uri = "ssh://192.0.2.16:22/"")
 * val exampleURI: ExampleURI = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleURI.serializer(), exampleURI)
 * ```
 */
public object URISerializer : KSerializer<URI> by stringSerializer("hocon.${URI::class.qualifiedName}", ::URI, URI::toASCIIString)
