---
layout: default
title: Nullable
permalink: /usage/deserialization/nullable
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Deserialization
grand_parent: Usage
nav_order: 3
---

Using `extract<T?>` is the better way than `Config.hasPath()`.
`extract<T?>` returns `T` when the path exists and `null` when it does not exist.
```kotlin
{% include_snippet deserialization-nullable %}
```
