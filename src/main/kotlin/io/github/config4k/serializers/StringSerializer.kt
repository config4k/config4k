package io.github.config4k.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder
import kotlinx.serialization.hocon.HoconEncoder

/**
 * Factory method to create a Serializer for types that can be serialized as string.
 *
 * @param serialName Serial name of descriptor
 * @param decode Function to decode typed value from string
 * @param encode Function to encode typed value to string
 */
internal inline fun <reified T> stringSerializer(
    serialName: String,
    crossinline decode: (String) -> T,
    crossinline encode: (T) -> String,
): KSerializer<T> =
    object : KSerializer<T> {
        override val descriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) =
            if (decoder is HoconDecoder) {
                decode(decoder.decodeString())
            } else {
                throw SerializationException("This class can be decoded only by Hocon format")
            }

        override fun serialize(
            encoder: Encoder,
            value: T,
        ) = if (encoder is HoconEncoder) {
            encoder.encodeString(encode(value))
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
