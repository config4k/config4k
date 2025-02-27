package io.github.config4k.serializers

import com.typesafe.config.ConfigValueType.OBJECT
import com.typesafe.config.ConfigValueType.STRING
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder
import kotlinx.serialization.hocon.HoconEncoder

/**
 * Serializer for [Regex].
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleRegex(
 *   @Serializable(RegexSerializer::class)
 *   val regex: Regex,
 * )
 * val config = ConfigFactory.parseString("regex = \"[0-9]{10}\"")
 * val exampleRegex: ExampleRegex = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleRegex.serializer(), exampleRegex)
 *
 * val configOps = ConfigFactory.parseString("regex = {pattern=\"[0-9]{10}\",options=[LITERAL]}")
 * val exampleRegexOps: ExampleRegex = Hocon.decodeFromConfig(configOps)
 * val newConfigOps = Hocon.encodeToConfig(ExampleRegex.serializer(), exampleRegexOps)
 * ```
 */
public object RegexSerializer : KSerializer<Regex> {
    @Serializable
    private data class SerializableRegex(
        private val pattern: String,
        private val options: Set<RegexOption>,
    ) {
        constructor(regex: Regex) : this(regex.pattern, regex.options)

        fun toRegex() = Regex(pattern, options)
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.kotlin.text.Regex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Regex =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path ->
                when (val type = conf.getValue(path).valueType()) {
                    STRING -> conf.getString(path).toRegex()
                    OBJECT -> {
                        decoder.decodeSerializableValue(SerializableRegex.serializer()).toRegex()
                    }

                    else -> throw SerializationException("kotlin.text.Regex can't be specified by $type value type")
                }
            }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: Regex,
    ) {
        if (encoder is HoconEncoder) {
            if (value.options.isEmpty()) {
                encoder.encodeString(value.pattern)
            } else {
                encoder.encodeSerializableValue(SerializableRegex.serializer(), SerializableRegex(value))
            }
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
