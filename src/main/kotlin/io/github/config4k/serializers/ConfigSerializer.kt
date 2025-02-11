package io.github.config4k.serializers

import com.typesafe.config.Config
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder
import kotlinx.serialization.hocon.HoconEncoder

/**
 * Serializer for [Config].
 * For decode using method [com.typesafe.config.Config.getConfig].
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleConfig(
 *   @Serializable(ConfigSerializer::class)
 *   val example: Config
 * )
 * val config = ConfigFactory.parseString("example: { conf: { key = value } }")
 * val exampleConfig: ExampleConfig = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(exampleConfig)
 * ```
 */
public object ConfigSerializer : KSerializer<Config> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.com.typesafe.config.Config", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Config =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path -> conf.getConfig(path) }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: Config,
    ) {
        if (encoder is HoconEncoder) {
            encoder.encodeConfigValue(value.root())
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
