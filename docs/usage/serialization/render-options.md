---
layout: default
title: ConfigRenderOptions
permalink: /usage/serialization/render-options
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Serialization
grand_parent: Usage
nav_order: 2
---

Typesafe Config's class `ConfigRenderOptions` is the argument of `ConfigValue#render`.
```kotlin
{% include_snippet serialization-hocon %}
```
Output:
```
    # hardcoded value
person {
    # hardcoded value
    age=20
    # hardcoded value
    name=foo
}
```

```kotlin
{% include_snippet serialization-without-comments %}
```
Output:
```
person {
    age=20
    name=foo
}
```
