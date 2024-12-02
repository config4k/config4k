package io.github.config4k

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

fun main() {
    DocExamples.delegatedProperties()
    DocExamples.deserializationMap()
    DocExamples.deserializationMapKey()
    DocExamples.deserializationDataClasses()
    DocExamples.deserializeNullable()
    DocExamples.deserializeEnum()
    DocExamples.serializeString()
    DocExamples.serializeHocon()
    DocExamples.serializeWithoutComments()
    DocExamples.extractDefaultValues()
}

object DocExamples {
    fun delegatedProperties() {
        // begin-snippet: delegated-properties
        val config =
            ConfigFactory.parseString(
                """
        |stringValue = hello
        |booleanValue = true
        |
                """.trimMargin(),
            )

        val stringValue: String by config
        println(stringValue) // hello

        val nullableStringValue: String? by config
        println(nullableStringValue) // null

        val booleanValue: Boolean by config
        println(booleanValue) // true
        // end-snippet
    }

    fun deserializationMap() {
        // begin-snippet: deserialization-map
        val config =
            ConfigFactory.parseString(
                """
        |map {  
        |  foo = 5
        |  bar = 6
        |}
                """.trimMargin(),
            )
        val map: Map<String, Int> = config.extract("map")
        println(map["foo"] == 5) // true
        println(map["bar"] == 6) // true
        // end-snippet
    }

    fun deserializationMapKey() {
        // begin-snippet: deserialization-map-key
        val config =
            ConfigFactory.parseString(
                """
        |map = [{  
        |  key = 5
        |  value = "foo"
        |}
        |{
        |  key = 6
        |  value = "bar"
        |}]
                """.trimMargin(),
            )
        val map: Map<Int, String> = config.extract("map")
        println(map[5] == "foo") // true
        println(map[6] == "bar") // true
        // end-snippet
    }

    fun deserializationDataClasses() {
        // begin-snippet: deserialization-data-class
        data class Person(
            val name: String,
            val age: Int,
        )

        val config =
            ConfigFactory.parseString(
                """
        |key {  
        |  name = "foo"
        |  age = 20
        |}
                """.trimMargin(),
            )
        val person: Person = config.extract("key")
        println(person.name) // foo
        println(person.age) // 20
        // end-snippet
    }

    fun deserializeNullable() {
        // begin-snippet: deserialization-nullable
        val config = ConfigFactory.parseString("""key = 10""")
        val key = config.extract<Int?>("key")
        val foo = config.extract<Int?>("foo")
        println(key) // 10
        println(foo) // null
        // end-snippet
    }

    // begin-snippet: deserialization-enum-class
    enum class Size {
        SMALL,
        MEDIUM,
        LARGE,
    }
    // end-snippet

    fun deserializeEnum() {
        // begin-snippet: deserialization-enum
        val config = ConfigFactory.parseString("""key = SMALL""")
        val small = config.extract<Size>("key")
        println(small == Size.SMALL) // true
        // end-snippet
    }

    fun serializeString() {
        // begin-snippet: serialization-string
        data class Person(
            val name: String,
            val age: Int,
        )

        val person = Person("foo", 20).toConfig("person")
        println(person.root().render())
        // end-snippet
    }

    fun serializeHocon() {
        // begin-snippet: serialization-hocon
        // If setJson(false) is called, ConfigValue.render returns HOCON
        data class Person(
            val name: String,
            val age: Int,
        )

        val person = Person("foo", 20).toConfig("person")
        val options = ConfigRenderOptions.defaults().setJson(false)
        println(person.root().render(options))
        // end-snippet
    }

    fun serializeWithoutComments() {
        // begin-snippet: serialization-without-comments
        // setOriginComments(false) removes comments
        data class Person(
            val name: String,
            val age: Int,
        )

        val person = Person("foo", 20).toConfig("person")
        val options =
            ConfigRenderOptions
                .defaults()
                .setJson(false)
                .setOriginComments(false)
        println(person.root().render(options))
        // end-snippet
    }

    fun extractDefaultValues() {
        // begin-snippet: deserialization-default-values
        val config = ConfigFactory.parseString("""key = 10""")
        val key = config.extract("unknown", 20)
        println(key) // 20
        // end-snippet
    }
}
