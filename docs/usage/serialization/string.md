---
layout: default
title: String
permalink: /usage/serialization/string
snippet_source: "../src/test/kotlin/io/github/config4k/DocExamples.kt"
snippet_comment_prefix: "//"
parent: Serialization
grand_parent: Usage
nav_order: 1
---

You can use [ConfigValue#render](https://lightbend.github.io/config/latest/api/com/typesafe/config/ConfigValue.html#render--) to serialize `Config`. Config4k helps getting `Config` of the class you want to serialize.
```kotlin
{% include_snippet serialization-string %}
```
Output:
```
{
    # hardcoded value
    "person" : {
        # hardcoded value
        "age" : 20,
        # hardcoded value
        "name" : "foo"
    }
}
```
