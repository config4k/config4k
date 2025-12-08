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
import java.util.regex.Pattern

/**
 * Serializer for [Pattern].
 *
 * Usage example:
 * ```
 * @Serializable
 * data class ExamplePattern(
 *   @Serializable(PatternSerializer::class)
 *   val pattern: Pattern,
 * )
 * val config = ConfigFactory.parseString("pattern = \"[0-9]{10}\"")
 * val examplePattern: ExamplePattern = Hocon.decodeFromConfig(config)
 * val newConfig = Hocon.encodeToConfig(ExamplePattern.serializer(), examplePattern)
 *
 * val configOps = ConfigFactory.parseString("pattern = {pattern=\"[0-9]{10}\",flags=[LITERAL]}")
 * val examplePatternOps: ExamplePattern = Hocon.decodeFromConfig(configOps)
 * val newConfigOps = Hocon.encodeToConfig(ExamplePattern.serializer(), examplePatternOps)
 * ```
 */
public object PatternSerializer : KSerializer<Pattern> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("hocon.java.util.regex.Pattern", PrimitiveKind.STRING)

    private enum class PatternFlag(
        val mask: Int,
    ) {
        CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE),
        MULTILINE(Pattern.MULTILINE),
        DOTALL(Pattern.DOTALL),
        UNICODE_CASE(Pattern.UNICODE_CASE),
        CANON_EQ(Pattern.CANON_EQ),
        UNIX_LINES(Pattern.UNIX_LINES),
        LITERAL(Pattern.LITERAL),
        UNICODE_CHARACTER_CLASS(Pattern.UNICODE_CHARACTER_CLASS),
        COMMENTS(Pattern.COMMENTS),
    }

    @Serializable
    private data class SerializablePattern(
        private val pattern: String,
        private val flags: Set<PatternFlag>,
    ) {
        constructor(pattern: Pattern) : this(
            pattern.pattern(),
            PatternFlag.entries.filter { pattern.flags() and it.mask == it.mask }.toSet(),
        )

        fun toPattern(): Pattern = Pattern.compile(pattern, flags.map { it.mask }.reduce(Int::or))
    }

    override fun deserialize(decoder: Decoder): Pattern =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path ->
                when (val type = conf.getValue(path).valueType()) {
                    STRING -> {
                        Pattern.compile(conf.getString(path))
                    }

                    OBJECT -> {
                        decoder.decodeSerializableValue(SerializablePattern.serializer()).toPattern()
                    }

                    else -> {
                        throw SerializationException("java.util.regex.Pattern can't be specified by $type value type")
                    }
                }
            }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: Pattern,
    ) {
        if (encoder is HoconEncoder) {
            if (value.flags() == 0) {
                encoder.encodeString(value.pattern())
            } else {
                encoder.encodeSerializableValue(SerializablePattern.serializer(), SerializablePattern(value))
            }
        } else {
            throw SerializationException("This class can be encoded only by Hocon format")
        }
    }
}
