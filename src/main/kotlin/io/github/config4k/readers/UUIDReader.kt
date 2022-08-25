package io.github.config4k.readers

import java.util.UUID

internal class UUIDReader : Reader<UUID>({ config, path -> UUID.fromString(config.getString(path)) })
