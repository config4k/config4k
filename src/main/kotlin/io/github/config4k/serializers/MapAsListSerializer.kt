package io.github.config4k.serializers

import com.typesafe.config.ConfigValueType.LIST
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder
import kotlinx.serialization.hocon.HoconEncoder

/**
 * Serializer for custom map, exposed as key value list.
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExampleMapWithKeyValue(
 *   @Serializable(MapAsListSerializer::class)
 *   val regex: Map<Int, String>,
 * )
 * val config = ConfigFactory.parseString("regex = [{key = 5, value = foo},{key = 6, value = bar}]")
 * val exampleMap: ExampleMapWithKeyValue = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExampleMapWithKeyValue.serializer(), exampleMap)
 * ```
 */
public class MapAsListSerializer<T, V>(
    private val keySerializer: KSerializer<T>,
    private val valueSerializer: KSerializer<V>,
) : KSerializer<Map<T, V>> {
    @Serializable
    private data class SerializableItem<T, V>(
        private val key: T,
        private val value: V,
    ) {
        fun toPair() = key to value
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.kotlin.collections.MapAsList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Map<T, V> =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path ->
                when (val type = conf.getValue(path).valueType()) {
                    LIST ->
                        decoder
                            .decodeSerializableValue(
                                ListSerializer(SerializableItem.serializer(keySerializer, valueSerializer)),
                            ).associate { it.toPair() }

                    else -> throw SerializationException("MapAsList can't be specified by $type value type, only LIST supported")
                }
            }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: Map<T, V>,
    ) {
        if (encoder is HoconEncoder) {
            encoder.encodeSerializableValue(
                ListSerializer(SerializableItem.serializer(keySerializer, valueSerializer)),
                value.map { SerializableItem(key = it.key, value = it.value) },
            )
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
