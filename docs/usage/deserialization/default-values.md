`Config.extract<T>` converts `Config` to `T`. Config4k also supports default values.
`extract<T, T?>` method returns `T` when the path exists and default value when it does not exist.
```kotlin
--8<-- "src/test/kotlin/io/github/config4k/DocExamples.kt:deserializationDefaultValues"
```
