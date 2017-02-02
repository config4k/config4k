package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec
import java.time.Duration
import java.util.*


class TestCollections : WordSpec() {
    init {
        val config = ConfigFactory.parseString("key = [0m, 1m, 1m, 2m]")
        "Config.extract" should {
            "return List" {
                val list = config.extract<List<Duration>>("key")
                list shouldBe listOf(Duration.ofMinutes(0),
                        Duration.ofMinutes(1),
                        Duration.ofMinutes(1),
                        Duration.ofMinutes(2))
            }
            
            "return List<List<Int>>" {
                val intListConfig =
                        ConfigFactory.parseString("key = [[0, 0], [1, 1]]")
                val list = intListConfig.extract<List<List<Int>>>("key")
                list shouldBe listOf(listOf(0, 0), listOf(1, 1))
            }

            "return Set" {
                val set = config.extract<Set<Duration>>("key")
                set should haveSize(3)
                set shouldBe setOf(Duration.ofMinutes(0),
                        Duration.ofMinutes(1),
                        Duration.ofMinutes(1),
                        Duration.ofMinutes(2))
            }

            "return Array<T>" {
                Arrays.deepEquals(
                        ConfigFactory

                                .parseString("""key = ["a", "b", "c", "d"]""")
                                .extract<Array<String>>("key"),
                        arrayOf("a", "b", "c", "d")) shouldBe true
                Arrays.deepEquals(
                        ConfigFactory
                                .parseString("""key = ["0m", "1m"]""")
                                .extract<Array<Duration>>("key"),
                        arrayOf(Duration.ofMinutes(0),
                                Duration.ofMinutes(1))) shouldBe true
            }
        }
    }
}