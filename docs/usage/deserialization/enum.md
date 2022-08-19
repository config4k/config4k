---
layout: default
title: Enum
permalink: /usage/deserialization/enum
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Deserialization
grand_parent: Usage
nav_order: 4
---

Config4k also supports Enum. Enum is converted to String of its name in the config file.
```kotlin
{% include_snippet deserialization-enum-class %}

{% include_snippet deserialization-enum %}
```
