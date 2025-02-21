package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder
import kotlinx.serialization.hocon.HoconEncoder
import kotlinx.serialization.hocon.serializers.JavaDurationSerializer
import java.time.Duration
import java.time.Period
import java.time.temporal.TemporalAmount

/**
 * Serializer for [TemporalAmount].
 *
 * Decoding uses [TemporalAmount format](https://github.com/lightbend/config/blob/main/HOCON.md#period-format).
 * This method will first try to get the value as a [Duration], and if unsuccessful, then as a [Period].
 * This means that values like "5m" will be parsed as 5 minutes rather than 5 months
 *
 * Encoding is available for two implementations: [Duration] and [Period].
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleTemporalAmount(
 *   @Serializable(TemporalAmountSerializer::class)
 *   val temporal: TemporalAmount,
 * )
 * val config = ConfigFactory.parseString("temporal = 1 y")
 * val exampleTemporalAmount: ExampleTemporalAmount = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleTemporalAmount.serializer(), exampleTemporalAmount)
 * ```
 */
public object TemporalAmountSerializer : KSerializer<TemporalAmount> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.java.time.temporal.TemporalAmount", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TemporalAmount =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path -> conf.getTemporal(path) }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: TemporalAmount,
    ) {
        if (encoder is HoconEncoder) {
            when (value) {
                is Duration -> JavaDurationSerializer.serialize(encoder, value)
                is Period -> PeriodSerializer.serialize(encoder, value)
                else -> throw SerializationException("Class ${value::class.java.name} can't be encoded by Hocon format")
            }
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
