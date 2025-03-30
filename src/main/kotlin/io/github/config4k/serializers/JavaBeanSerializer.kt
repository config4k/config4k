package io.github.config4k.serializers

import com.typesafe.config.Config
import com.typesafe.config.ConfigBeanFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.hocon.HoconDecoder

/**
 * Serializer for Java Bean objects. Supports only decoding of objects from [Config].
 * [JavaBeanSerializer] using [com.typesafe.config.ConfigBeanFactory.create] to decode Java Bean objects.
 * To create a [JavaBeanSerializer], use the method [io.github.config4k.javaBeanSerializer]
 * Usage example:
 *
 * Java Bean code
 * ```
 * public class Person {
 *  private String name;
 *  private int age;
 *  // getters and setters
 * }
 * ```
 * Kotlin code
 * ```
 * object PersonSerializer : KSerializer<Person> by javaBeanSerializer()
 *
 * @Serializable
 * data class ExampleJavaBean(
 *  @Serializable(with = PersonSerializer::class)
 *  val person: Person
 * )
 * val config = ConfigFactory.parseString("""
 *  person: {
 *      name = Alex
 *      age = 24
 *  }
 * """.trimIndent())
 * val exampleJavaBean: ExampleJavaBean = Hocon.decodeFromConfig(config)
 * ```
 */
public class JavaBeanSerializer<T>(
    private val clazz: Class<T>,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hocon.java.bean", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T =
        if (decoder is HoconDecoder) {
            decoder.decodeConfigValue { conf, path ->
                ConfigBeanFactory.create(conf.getConfig(path), clazz)
            }
        } else {
            throw SerializationException("This class can be decoded only by Hocon format")
        }

    override fun serialize(
        encoder: Encoder,
        value: T,
    ): Unit = throw SerializationException("JavaBeanSerializer is only used for deserialization")
}
