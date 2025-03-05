Typesafe Config's class `ConfigRenderOptions` is the argument of `ConfigValue#render`.
```kotlin
--8<-- "src/test/kotlin/io/github/config4k/DocExamples.kt:serializationHocon"
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
--8<-- "src/test/kotlin/io/github/config4k/DocExamples.kt:serializationWithoutComments"
```
Output:
```
person {
    age=20
    name=foo
}
```
