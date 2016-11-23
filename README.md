# Config4k

[![Build Status](https://travis-ci.org/config4k/config4k.svg?branch=master)](https://travis-ci.org/config4k/config4k) [![codecov](https://codecov.io/gh/config4k/config4k/branch/master/graph/badge.svg)](https://codecov.io/gh/config4k/config4k) [![codebeat badge](https://codebeat.co/badges/4e9682a1-cdbb-4e1f-804b-a2d801381942)](https://codebeat.co/projects/github-com-config4k-config4k) [![kotlin](https://img.shields.io/badge/kotlin-1.0.4-pink.svg)]() [ ![Download](https://api.bintray.com/packages/config4k/config4k/config4k/images/download.svg?version=0.1.0) ](https://bintray.com/config4k/config4k/config4k/0.1.0/link)

A Typesafe Config wrapper for Kotlin. Config4k adds an extension function `extract<T>` to `com.typesafe.config.Config`. This enables you to write like this `Config.extract<String>`
## How to use

 
Gradle:

`compile 'io.github.config4k:config4k:0.1.0'`

Maven:

```xml
<dependency>
  <groupId>io.github.config4k</groupId>
  <artifactId>config4k</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

## Examples

```kotlin
import com.typesafe.config.ConfigFactory
import io.github.config4k.*

fun main(args: Array<String>) {
    val config = ConfigFactory.parseString("""key = "config4k" """)
    val key: String = config.extract<String>("key")
    println(key)
}
```

## Supported types
- Primitive types
     - `Boolean`
     - `Int`
     - `Long`
     - `Double`
- `String`
- `java.time.Duration`
- Typesafe Config classes
    - `com.typesafe.config.Config`
    - `com.typesafe.config.ConfigValue`
    
## Contribute
PRs accepted.
