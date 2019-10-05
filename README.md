# Config4k

[![Build Status](https://travis-ci.org/config4k/config4k.svg?branch=master)](https://travis-ci.org/config4k/config4k) [![codecov](https://codecov.io/gh/config4k/config4k/branch/master/graph/badge.svg)](https://codecov.io/gh/config4k/config4k) [![codebeat badge](https://codebeat.co/badges/4e9682a1-cdbb-4e1f-804b-a2d801381942)](https://codebeat.co/projects/github-com-config4k-config4k) [![kotlin](https://img.shields.io/badge/kotlin-1.3.10-pink.svg)]() [ ![Download](https://api.bintray.com/packages/config4k/config4k/config4k/images/download.svg) ](https://bintray.com/config4k/config4k/config4k/_latestVersion)

_**Config** for **K**otlin._  

**Config4k** is a lightweight [Typesafe Config](https://github.com/typesafehub/config) wrapper for Kotlin and inspired by [ficus](https://github.com/iheartradio/ficus),  providing simple extension functions `Config.extract<T>` and `Any.toConfig` to convert between `Config` and Kotlin Objects.

![example](https://user-images.githubusercontent.com/21121197/66254407-df696f00-e7b0-11e9-839d-e47c9da16807.png)

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
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
    jcenter()
}


dependencies {
    compile 'io.github.config4k:config4k:xxx' // See the `Download` badge
}
```
## Usage
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
val person: Map<String, Int> = config.extract<Map<String, Int>>("map")
map["foo"] == 5 // true
map["bar"] == 6 // true
```
or with arbitrary keys
```kotlin
val config = ConfigFactory.parseString("""
                                          |map [{  
                                          |  key = 5
                                          |  value = "foo"
                                          |}
                                          |{
                                          |  key = 6
                                          |  value = "bar"
                                          |}]""".trimMargin())
val person: Map<Int, String> = config.extract<Map<Int, String>>("map")
map[5] == "foo" // true
map[6] == "bar" // true
```
Test Class: [TestMap.kt](https://github.com/config4k/config4k/blob/master/src/test/io/github/config4k/TestMap.kt)
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
person.name == "foo" // true
person.age == 20 // true
```
For more details, please see [TestArbitraryType.kt](https://github.com/config4k/config4k/blob/master/src/test/io/github/config4k/TestArbitraryType.kt)
#### Nullable
Using `extract<T?>` is the better way than `Config.hasPath()`.
`extract<T?>` returns `T` when the path exists and `null` when it does not exist.
```kotlin
val config = ConfigFactory.parseString("""key = 10""")
val key = config.extract<Int?>("key")
val foo = config.extract<Int?>("foo")
key == 10 // true
foo == null // true
```
Test Class: [TestNullable.kt](https://github.com/config4k/config4k/blob/master/src/test/io/github/config4k/TestNullable.kt)
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
small == Size.SMALL // true
```
Test Class: [TestEnum.kt](https://github.com/config4k/config4k/blob/master/src/test/io/github/config4k/TestEnum.kt)
### Serialization
`Any.toConfig` converts the receiver object to `Config`.
#### String
You can use [ConfigValue.render()](https://typesafehub.github.io/config/latest/api/com/typesafe/config/ConfigValue.html#render--) to serialize `Config`. Config4k helps getting `Config` of the class you want to serialize.
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
Test Class: [TestToConfigForArbitraryType.kt](https://github.com/config4k/config4k/blob/master/src/test/io/github/config4k/TestToConfigForArbitraryType.kt)
#### ConfigRenderOptions
Typesafe Config's class `ConfigRenderOptions` is the argument of `ConfigValue.render`.
```kotlin
// If setJson(false) is called, ConfigValue.render returns HOCON
val options = ConfigRenderOptions.defaults().setJson(false)
println(person.root().render(option))
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
val options = ConfigRenderOptions.defaults()
                        .setJson(false)
                        .setOriginComments(false)
println(person.root().render(option))
```
Output:
```
person {
    age=20
    name=foo
}
```

## Supported types
`extract` and `toConfig` support these types.
- Primitive types
     - `Boolean`
     - `Int`
     - `Long`
     - `Double`
- `String`
- `java.time.Duration`
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
- Enum
- Data classes

## Contribute
Would you like to contribute to Config4k?  
Take a look at [CONTRIBUTING.md](https://github.com/config4k/config4k/blob/master/CONTRIBUTING.md)
