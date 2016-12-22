package io.github.config4k

import com.typesafe.config.ConfigFactory
import io.kotlintest.specs.WordSpec
import java.time.Duration
import java.util.*


class TestCollections : WordSpec() {
    init {
        val config = ConfigFactory.parseString("key = [0, 1, 1, 2]")
        "Config.extract" should {
            "return List" {
                val list = config.extract<List<Int>>("key")
                list shouldBe listOf(0, 1, 1, 2)
            }

            "return Set" {
                val set = config.extract<Set<Int>>("key")
                set should haveSize(3)
                set shouldBe setOf(0, 1, 2)
            }

            "return Array<T>" {
                Arrays.deepEquals(
                        ConfigFactory
                                .parseString("""key = ["a", "b", "c", "d"]""")
                                .extract<Array<String>>("key"),
                        arrayOf("a", "b", "c", "d"))
                Arrays.deepEquals(
                        ConfigFactory
                                .parseString("""key = ["0m", "1m"]""")
                                .extract<Array<Duration>>("key"),
                        arrayOf(Duration.ofMinutes(0), Duration.ofMinutes(1)))
            }
        }
    }
}