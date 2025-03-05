You can
use [ConfigValue#render](https://lightbend.github.io/config/latest/api/com/typesafe/config/ConfigValue.html#render--)
to serialize `Config`. Config4k helps getting `Config` of the class you want to serialize.
`Any.toConfig` converts the receiver object to `Config`.

```kotlin
--8<-- "src/test/kotlin/io/github/config4k/DocExamples.kt:serializationString"
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
