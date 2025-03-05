Using `extract<T?>` is the better way than `Config.hasPath()`.
`extract<T?>` returns `T` when the path exists and `null` when it does not exist.
```kotlin
--8<-- "src/test/kotlin/io/github/config4k/DocExamples.kt:deserializationNullable"
```
