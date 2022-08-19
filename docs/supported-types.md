---
layout: default
title: Supported types
permalink: /supported-types
nav_order: 4
---

Property delegation, `extract` and `toConfig` support these types:
- `Boolean`
- `Byte`
- `Int`
- `Long`
- `Float`
- `Double`
- `String`
- `java.io.File`
- `java.nio.file.Path`
- `java.time.Duration`
- `java.time.Period`
- `java.time.temporal.TemporalAmount`
- `kotlin.text.Regex`
- Collections
    - `List`
    - `Set`
    - `Map<K, V>`
    - `Array<T>` (You can use `Array<Int>`, but can't use `Array<Array<Int>>`)
- Nullable `T?`
- Typesafe Config classes(Calling `toConfig` is meaningless)
    - `com.typesafe.config.Config`
    - `com.typesafe.config.ConfigValue`
    - `com.typesafe.config.ConfigMemorySize`
- Enum
- Data classes
