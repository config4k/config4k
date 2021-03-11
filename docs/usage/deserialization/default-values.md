---
layout: default
title: Default values
permalink: /usage/deserialization/default-values
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Deserialization
grand_parent: Usage
nav_order: 4
---

Config4k also supports default values.
`extract<T, T?>` method returns `T` when the path exists and default value when it does not exist.
```kotlin
{% include_snippet deserialization-default-values %}
```
