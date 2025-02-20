package io.github.config4k.serializers

import com.typesafe.config.ConfigValue
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
 * Serializer for [ConfigValue].
 * For decode using method [com.typesafe.config.Config.getValue].
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleConfigValue(
 *  @Serializable(ConfigValueSerializer::class)
 *  val conf: ConfigValue
 * )
 * val config = ConfigFactory.parseString("conf: { key = value }")
 * val exampleConfigValue: ExampleConfigValue = Hocon.decodeFromConfig(config)
 * val newConfigValue = Hocon.encodeToConfig(exampleConfigValue)
 * ```
 */
public object ConfigValueSerializer : KSerializer<ConfigValue> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.com.typesafe.config.ConfigValue", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ConfigValue =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path -> conf.getValue(path) }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: ConfigValue,
    ) {
        if (encoder is HoconEncoder) {
            encoder.encodeConfigValue(value)
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
