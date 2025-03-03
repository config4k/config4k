package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.invariantSeparatorsPathString

/**
 * Serializer for [Path]
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExamplePath(
 *   @Serializable(PathSerializer::class)
 *   val path: Path,
 * )
 * val config = ConfigFactory.parseString("path = /tmp/file")
 * val examplePath: ExamplePath = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExamplePath.serializer(), examplePath)
 * ```
 */
public object PathSerializer : KSerializer<Path> by stringSerializer(
    serialName = "hocon.${Path::class.qualifiedName}",
    decode = { Paths.get(it) },
    encode = Path::invariantSeparatorsPathString,
)
