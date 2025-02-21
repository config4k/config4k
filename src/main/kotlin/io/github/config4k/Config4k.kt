package io.github.config4k

import io.github.config4k.serializers.ConfigSerializer
import io.github.config4k.serializers.ConfigValueSerializer
import io.github.config4k.serializers.PeriodSerializer
import io.github.config4k.serializers.RegexSerializer
import io.github.config4k.serializers.TemporalAmountSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.serializers.ConfigMemorySizeSerializer
import kotlinx.serialization.hocon.serializers.JavaDurationSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

public val Config4kModule: SerializersModule =
    SerializersModule {
        contextual(JavaDurationSerializer)
        contextual(PeriodSerializer)
        contextual(RegexSerializer)
        contextual(TemporalAmountSerializer)
        contextual(ConfigSerializer)
        contextual(ConfigValueSerializer)
        contextual(ConfigMemorySizeSerializer)
    }

public val Config4k: Hocon =
    Hocon {
        serializersModule = Config4kModule
    }
