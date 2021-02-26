# Config4k

[![Build Status](https://travis-ci.org/config4k/config4k.svg?branch=master)](https://travis-ci.org/config4k/config4k)
[![codecov](https://codecov.io/gh/config4k/config4k/branch/master/graph/badge.svg)](https://codecov.io/gh/config4k/config4k)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.config4k/config4k.svg?label=latest&color=blue)](https://search.maven.org/search?q=a:config4k%20AND%20g:io.github.config4k)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.config4k/config4k?label=snapshot&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/io/github/config4k/config4k/)
[![javadoc](https://javadoc.io/badge2/io.github.config4k/config4k/javadoc.svg)](https://javadoc.io/doc/io.github.config4k/config4k)
[![GitHub](https://img.shields.io/github/license/config4k/config4k)](https://github.com/config4k/config4k/blob/master/LICENSE)

_**Config** for **K**otlin._

**Config4k** is a lightweight [Typesafe Config](https://github.com/typesafehub/config) wrapper for Kotlin and inspired by [ficus](https://github.com/iheartradio/ficus),  providing simple extension functions `Config.extract<T>` and `Any.toConfig` to convert between `Config` and Kotlin Objects.

![example](https://user-images.githubusercontent.com/21121197/66254407-df696f00-e7b0-11e9-839d-e47c9da16807.png)

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
  - [Delegated Properties](#delegated-properties)
  - [Deserialization](#deserialization)
    - [Data Classes](#data-classes)
    - [Nullable](#nullable)
    - [Enum](#enum)
  - [Serialization](#serialization)
    - [String](#string)
    - [ConfigRenderOptions](#configrenderoptions)
- [Supported types](#supported-types)
- [Contribute](#contribute)
## Installation

Gradle:

```groovy
repositories {
    mavenCentral()
}


dependencies {
    compile 'io.github.config4k:config4k:xxx' // See the `Download` badge
}
```
## Usage

### Delegated Properties

By far the simplest way to use config4k is via [Kotlin Delegated Properties](https://kotlinlang.org/docs/reference/delegated-properties.html):

```kotlin
val config = ConfigFactory.parseString("""
                                          |stringValue = hello
                                          |booleanValue = true
                                          |""".trimMargin())

val stringValue: String by config
println(stringValue) // hello

val nullableStringValue: String? by config
println(nullableStringValue) // null

val booleanValue: Boolean by config
println(booleanValue) // true
```

### Deserialization
`Config.extract<T>` converts `Config` to `T`.
#### Map
Maps can be serialized with `String` keys
```kotlin
val config = ConfigFactory.parseString("""
                                          |map {  
                                          |  foo = 5
                                          |  bar = 6
                                          |}""".trimMargin())
val map: Map<String, Int> = config.extract<Map<String, Int>>("map")
println(map["foo"] == 5) // true
println(map["bar"] == 6) // true
```
or with arbitrary keys
```kotlin
val config = ConfigFactory.parseString("""
                                          |map = [{  
                                          |  key = 5
                                          |  value = "foo"
                                          |}
                                          |{
                                          |  key = 6
                                          |  value = "bar"
                                          |}]""".trimMargin())
val map: Map<Int, String> = config.extract<Map<Int, String>>("map")
println(map[5] == "foo") // true
println(map[6] == "bar") // true
```
Test Class: [TestMap.kt](src/test/kotlin/io/github/config4k/TestMap.kt)
#### Data Classes
Config4k has no option to use different names between code and config file.
```kotlin
data class Person(val name: String, val age: Int)

val config = ConfigFactory.parseString("""
                                          |key {  
                                          |  name = "foo"
                                          |  age = 20
                                          |}""".trimMargin())
val person: Person = config.extract<Person>("key")
println(person.name == "foo") // true
println(person.age == 20) // true
```
For more details, please see [TestArbitraryType.kt](src/test/kotlin/io/github/config4k/TestArbitraryType.kt)
#### Nullable
Using `extract<T?>` is the better way than `Config.hasPath()`.
`extract<T?>` returns `T` when the path exists and `null` when it does not exist.
```kotlin
val config = ConfigFactory.parseString("""key = 10""")
val key = config.extract<Int?>("key")
val foo = config.extract<Int?>("foo")
println(key == 10) // true
println(foo == null) // true
```
Test Class: [TestNullable.kt](src/test/kotlin/io/github/config4k/TestNullable.kt)
#### Enum
Config4k also supports Enum. Enum is converted to String of its name in the config file.
```kotlin
enum class Size {
    SMALL,
    MEDIUM,
    LARGE
}

val config = ConfigFactory.parseString("""key = SMALL""")
val small = config.extract<Size>("key")
println(small == Size.SMALL) // true
```
Test Class: [TestEnum.kt](src/test/kotlin/io/github/config4k/TestEnum.kt)
### Serialization
`Any.toConfig` converts the receiver object to `Config`.
#### String
You can use [ConfigValue.render()](https://lightbend.github.io/config/latest/api/com/typesafe/config/ConfigValue.html#render--) to serialize `Config`. Config4k helps getting `Config` of the class you want to serialize.
```kotlin
data class Person(val name: String, val age: Int)
val person = Person("foo", 20).toConfig("person")
println(person.root().render())
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
Test Class: [TestToConfigForArbitraryType.kt](src/test/kotlin/io/github/config4k/TestToConfigForArbitraryType.kt)
#### ConfigRenderOptions
Typesafe Config's class `ConfigRenderOptions` is the argument of `ConfigValue.render`.
```kotlin
// If setJson(false) is called, ConfigValue.render returns HOCON
data class Person(val name: String, val age: Int)
val person = Person("foo", 20).toConfig("person")
val options = ConfigRenderOptions.defaults().setJson(false)
println(person.root().render(options))
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
// setOriginComments(false) removes comments
data class Person(val name: String, val age: Int)
val person = Person("foo", 20).toConfig("person")
val options = ConfigRenderOptions.defaults()
                        .setJson(false)
                        .setOriginComments(false)
println(person.root().render(options))
```
Output:
```
person {
    age=20
    name=foo
}
```

## Supported types
Property delegation, `extract` and `toConfig` support these types:
- Primitive types
     - `Boolean`
     - `Byte`
     - `Int`
     - `Long`
     - `Float`
     - `Double`
- `String`
- `import java.io.File`
- `import java.nio.file.Path`
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

See [SelectReader.kt](src/main/kotlin/io/github/config4k/readers/SelectReader.kt) for the exhaustive list.

## Snapshots

All **snapshot** artifacts are available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/io/github/config4k/config4k/).

## Contribute
Would you like to contribute to Config4k?  
Take a look at [CONTRIBUTING.md](CONTRIBUTING.md)
