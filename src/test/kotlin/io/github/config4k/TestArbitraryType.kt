package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.net.URL
import java.time.Duration
import java.util.UUID

class TestArbitraryType :
    WordSpec({
        "Config.extract<Person>" should {
            "return Person" {
                val config =
                    """
                key = {  
                  name = "foo"
                  age = 20
                }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 20)
            }
            "work if optional argument is omitted" {
                val config =
                    """
                key = {  
                  name = "foo"
                }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", 10)
            }
            "make optional argument null if there is a key having null" {
                val config =
                    """
                key = {  
                  name = "foo"
                  age = null
                }""".toConfig()
                val person = config.extract<Person>("key")
                person shouldBe Person("foo", null)
            }
        }

        "Config.extract<PrivateEye>" should {
            "return private data class PrivateEye" {
                val config =
                    """
                key = {
                  target = "criminal"
                }""".toConfig()
                val person = config.extract<PrivateEye>("key")
                person shouldBe PrivateEye("criminal")
            }
        }

        "Config.extract<Nest>" should {
            "return Nest" {
                val config =
                    """
                key = {  
                  nest = 1
                  person = {
                    name = "foo"
                    age = 20
                  }
                }""".toConfig()
                val person = config.extract<Nest>("key")
                person shouldBe Nest(1, Person("foo", 20))
            }
        }

        "Config.extract<WholeConfig>()" should {
            "return WholeConfig without path" {
                val config =
                    """
                key = {
                  name = "foo"
                  age = 20
                }""".toConfig()
                val wholeConfig = config.extract<WholeConfig>()
                wholeConfig shouldBe WholeConfig(Person("foo", 20))
            }
        }

        "Config.extract<NestHyphenated>()" should {
            "return NestHyphenated without path" {
                val config =
                    """
                {
                  nested-person = {
                     name = "foo"
                     age = 20
                   }
                }""".toConfig()
                val nestHyphenated = config.extract<NestHyphenated>()
                nestHyphenated shouldBe NestHyphenated(Person("foo", 20))
            }
        }

        "Config.extract<TestJavaBean>()" should {
            "return TestJavaBean" {
                val config =
                    """
                {
                    name = "foo"
                    age = 20
                }""".toConfig()
                val javaBean = config.extract<TestJavaBean>()
                javaBean.name shouldBe "foo"
                javaBean.age shouldBe 20
            }
        }

        "Config.extract<NestJavaBean>()" should {
            "return TestJavaBean" {
                val config =
                    """
                {
                  person {
                    name = "foo"
                    age = 20
                  }
                }""".toConfig()
                val nestJavaBean = config.extract<NestJavaBean>()
                nestJavaBean.person.name shouldBe "foo"
                nestJavaBean.person.age shouldBe 20
                nestJavaBean.person2 shouldBe null
            }
        }

        "Config.extract<DataWithUUID>()" should {
            "return DataWithUUID" {
                val config =
                    """
                {
                  uuid = 3f5f1d2f-38b7-4a14-9e67-c618d8f83189
                }""".toConfig()
                val data = config.extract<DataWithUUID>()
                data shouldBe DataWithUUID(UUID.fromString("3f5f1d2f-38b7-4a14-9e67-c618d8f83189"))
            }
        }

        "Config.extract<DataWithURL>()" should {
            "return DataWithURL" {
                val url = "https://config4k.github.io/config4k/"
                val config =
                    """
                {
                  url = "$url"
                }""".toConfig()
                val data = config.extract<DataWithURL>()
                data.url.toString() shouldBe url
            }
        }

        "Config.extract<NamingStrategy>()" should {
            "return NamingStrategy" {
                val config =
                    """
                {
                  fizzBuzz: "fizzBuzz"
                  foo-bar: "fooBar"
                }""".toConfig()
                val data = config.extract<NamingStrategy>()
                data.fizzBuzz shouldBe "fizzBuzz"
                data.fooBar shouldBe "fooBar"
            }
            "work with a path" {
                val config =
                    """
                app {
                  fizzBuzz: "fizzBuzz"
                  foo-bar: "fooBar"
                }""".toConfig()
                val data = config.extract<NamingStrategy>("app")
                data.fizzBuzz shouldBe "fizzBuzz"
                data.fooBar shouldBe "fooBar"
            }
        }
    })

data class Person(
    val name: String,
    val age: Int? = 10,
)

private data class PrivateEye(
    val target: String,
)

data class Nest(
    val nest: Int,
    val person: Person,
)

data class WholeConfig(
    val key: Person,
)

data class NestHyphenated(
    val nestedPerson: Person,
)

data class NestJavaBean(
    val person: TestJavaBean,
    val person2: TestJavaBean?,
)

data class DataWithUUID(
    val uuid: UUID,
)

data class DataWithDuration(
    val duration: Duration,
)

data class DataWithURL(
    val url: URL,
)

data class NamingStrategy(
    val fizzBuzz: String? = null,
    val fooBar: String? = null,
)
