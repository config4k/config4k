package io.github.config4k

import io.github.config4k.serializers.ConfigSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

public val Config4k: Hocon =
    Hocon { serializersModule = SerializersModule { contextual(ConfigSerializer) } }
