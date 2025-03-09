@file:Suppress("ktlint:standard:filename")

package io.github.config4k

import io.github.config4k.serializers.MapAsListSerializer
import kotlinx.serialization.Serializable

public typealias MapAsList<K, V> =
    @Serializable(with = MapAsListSerializer::class)
    Map<K, V>
