package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import java.io.File

/**
 * Serializer for [File]
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleFile(
 *   @Serializable(FileSerializer::class)
 *   val file: File,
 * )
 * val config = ConfigFactory.parseString("file = /file")
 * val exampleFile: ExampleFile = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleFile.serializer(), exampleFile)
 * ```
 */
public object FileSerializer : KSerializer<File> by stringSerializer(
    serialName = "hocon.${File::class.qualifiedName}",
    decode = { File(it) },
    encode = File::invariantSeparatorsPath,
)
