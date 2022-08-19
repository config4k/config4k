---
layout: default
title: Map
permalink: /usage/deserialization/map
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Deserialization
grand_parent: Usage
nav_order: 1
---

Maps can be serialized with `String` keys
```kotlin
{% include_snippet deserialization-map %}
```
or with arbitrary keys
```kotlin
{% include_snippet deserialization-map-key %}

```
