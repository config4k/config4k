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
        // # --8<-- [start:delegatedProperties]
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
        // # --8<-- [end:delegatedProperties]
    }

    fun deserializationMap() {
        // --8<-- [start:deserializationMap]
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
        // --8<-- [end:deserializationMap]
    }

    fun deserializationMapKey() {
        // --8<-- [start:deserializationMapKey]
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
        // --8<-- [end:deserializationMapKey]
    }

    fun deserializationDataClasses() {
        // --8<-- [start:deserializationDataClass]
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
        // --8<-- [end:deserializationDataClass]
    }

    fun deserializeNullable() {
        // --8<-- [start:deserializationNullable]
        val config = ConfigFactory.parseString("""key = 10""")
        val key = config.extract<Int?>("key")
        val foo = config.extract<Int?>("foo")
        println(key) // 10
        println(foo) // null
        // --8<-- [end:deserializationNullable]
    }

    // --8<-- [start:deserializationEnumClass]
    enum class Size {
        SMALL,
        MEDIUM,
        LARGE,
    }
    // --8<-- [end:deserializationEnumClass]

    fun deserializeEnum() {
        // --8<-- [start:deserializationEnum]
        val config = ConfigFactory.parseString("""key = SMALL""")
        val small = config.extract<Size>("key")
        println(small == Size.SMALL) // true
        // --8<-- [end:deserializationEnum]
    }

    fun serializeString() {
        // --8<-- [start:serializationString]
        data class Person(
            val name: String,
            val age: Int,
        )

        val person = Person("foo", 20).toConfig("person")
        println(person.root().render())
        // --8<-- [end:serializationString]
    }

    fun serializeHocon() {
        // --8<-- [start:serializationHocon]
        // If setJson(false) is called, ConfigValue.render returns HOCON
        data class Person(
            val name: String,
            val age: Int,
        )

        val person = Person("foo", 20).toConfig("person")
        val options = ConfigRenderOptions.defaults().setJson(false)
        println(person.root().render(options))
        // --8<-- [end:serializationHocon]
    }

    fun serializeWithoutComments() {
        // --8<-- [start:serializationWithoutComments]
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
        // --8<-- [end:serializationWithoutComments]
    }

    fun extractDefaultValues() {
        // --8<-- [start:deserializationDefaultValues]
        val config = ConfigFactory.parseString("""key = 10""")
        val key = config.extract("unknown", 20)
        println(key) // 20
        // --8<-- [end:deserializationDefaultValues]
    }
}
