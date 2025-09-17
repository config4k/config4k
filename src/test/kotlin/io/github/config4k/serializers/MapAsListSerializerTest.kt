package io.github.config4k.serializers

import io.github.config4k.Config4k
import io.github.config4k.MapAsList
import io.github.config4k.render
import io.github.config4k.serializers.MapAsListSerializerTest.Protocol.HTTP
import io.github.config4k.serializers.MapAsListSerializerTest.Protocol.HTTPS
import io.github.config4k.toConfig
import io.kotest.matchers.should
import io.mockk.mockk
import kotlinx.serialization.Contextual
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import java.net.URL
import java.util.UUID

class MapAsListSerializerTest {
    private companion object {
        val simpleKeyMap = mapOf(5 to NestedConf(foo = "bar"), 0 to null, null to NestedConf(foo = "baz"))
        val enumKeyMap = mapOf(HTTPS to "secured", HTTP to "unsecured")
        val typedKeyMap = mapOf(UUID.fromString("b14240e7-acc0-4a19-bc5d-31a7901e36b0") to URI("https://www.example.com").toURL())
    }

    @Serializable
    private data class NestedConf(
        val foo: String,
    )

    @Serializable
    private enum class Protocol {
        HTTPS,
        HTTP,
    }

    @Test
    fun checkSimpleKeyDecoding() {
        @Serializable
        data class Conf(
            val map: MapAsList<Int?, NestedConf?>,
        )

        val test: Conf =
            Config4k.decodeFromConfig(
                """map = [
                    |{key = 5, value = {"foo" = "bar"}},
                    |{key = 0, value = null}, 
                    |{key = null, value = {"foo" = "baz"}}
                    |]
                """.trimMargin()
                    .toConfig(),
            )

        assertThat(test.map).isEqualTo(simpleKeyMap)

        assertThatThrownBy {
            Config4k.decodeFromConfig<Conf>("map = { foo = bar }".toConfig())
        }.isInstanceOf(SerializationException::class.java)
            .hasMessage("MapAsList can't be specified by OBJECT value type, only LIST supported")

        assertThrows<MissingFieldException> { Config4k.decodeFromConfig<Conf>("value = bar".toConfig()) }
            .should { assertThat(it.missingFields).contains("map") }
    }

    @Test
    fun checkSimpleKeyEncoding() {
        @Serializable
        data class Conf(
            val map: MapAsList<Int?, NestedConf?>,
        )

        var test = Config4k.encodeToConfig(Conf(simpleKeyMap))
        assertThat(test.render()).isEqualTo("map=[{key=5,value{foo=bar}},{key=0,value=null},{key=null,value{foo=baz}}]")
        test = Config4k.encodeToConfig(Conf(emptyMap()))
        assertThat(test.render()).isEqualTo("map=[]")
    }

    @Test
    fun checkEnumKeyDecoding() {
        @Serializable
        data class Conf(
            val map: MapAsList<@Contextual Protocol, @Contextual String>,
        )

        val test: Conf =
            Config4k.decodeFromConfig(
                "map = [{key = HTTPS, value = secured}, {key = HTTP, value = unsecured}]".toConfig(),
            )

        assertThatThrownBy {
            Config4k.decodeFromConfig<Conf>("map = [{ key = HTTP }]".toConfig())
        }.isInstanceOf(SerializationException::class.java)
            .hasMessage(
                "Field 'value' is required for type with serial name 'io.github.config4k.serializers.MapAsListSerializer.SerializableItem', but it was missing",
            )

        assertThatThrownBy {
            Config4k.decodeFromConfig<Conf>("map = [{ value = secured }]".toConfig())
        }.isInstanceOf(SerializationException::class.java)
            .hasMessage(
                "Field 'key' is required for type with serial name 'io.github.config4k.serializers.MapAsListSerializer.SerializableItem', but it was missing",
            )

        assertThat(test.map).isEqualTo(enumKeyMap)
    }

    @Test
    fun checkTypedMapDecoding() {
        @Serializable
        data class Conf(
            val map: MapAsList<@Contextual UUID, @Contextual URL>,
        )

        val test: Conf =
            Config4k.decodeFromConfig(
                "map = [{key = b14240e7-acc0-4a19-bc5d-31a7901e36b0, value = \"https://www.example.com\"}]".toConfig(),
            )

        assertThat(test.map).isEqualTo(typedKeyMap)
    }

    @Test
    fun checkIncorrectEncoder() {
        assertThatThrownBy { MapAsListSerializer(String.serializer(), String.serializer()).serialize(mockk(), mockk()) }
            .isInstanceOf(SerializationException::class.java)
            .hasMessage("This class can be encoded only by Hocon format")
    }

    @Test
    fun checkIncorrectDecoder() {
        assertThatThrownBy { MapAsListSerializer(String.serializer(), String.serializer()).deserialize(mockk()) }
            .isInstanceOf(SerializationException::class.java)
            .hasMessage("This class can be decoded only by Hocon format")
    }
}
