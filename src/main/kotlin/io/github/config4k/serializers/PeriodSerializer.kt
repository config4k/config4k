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
import java.time.Period

/**
 * Serializer for [Period].
 *
 * Decoding uses [Period format](https://github.com/lightbend/config/blob/main/HOCON.md#period-format).
 *
 * Encoding uses unit strings for period: d, m, y.
 * Encoding uses the largest period unit.
 * Encoding [Period] that contains a day along with a month or year is not possible, because only one period unit is allowed in HOCON.
 *
 * Example:
 * * 12 months -> 1 year
 * * 1 year 6 months -> 18 months
 * * 12 days -> 12 days
 * * 1 year 12 days -> throw SerializationException
 * * 10 months 2 days -> throw SerializationException
 * * 1 year 5 month 5 days -> throw SerializationException
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExamplePeriod(
 *   @Serializable(PeriodSerializer::class)
 *   val period: Period,
 * )
 * val config = ConfigFactory.parseString("period = 1 y")
 * val examplePeriod: ExamplePeriod = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExamplePeriod.serializer(), examplePeriod)
 * ```
 */
public object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.java.time.Period", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Period =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path -> conf.getPeriod(path) }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: Period,
    ) {
        if (encoder is HoconEncoder) {
            val period =
                value.normalized().let {
                    if (it.years == 0 && it.months == 0) {
                        "${it.days} d"
                    } else if (it.days == 0) {
                        if (it.months == 0) "${it.years} y" else "${it.toTotalMonths()} m"
                    } else {
                        throw SerializationException("java.time.Period can be specified by only one time unit")
                    }
                }
            encoder.encodeString(period)
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
