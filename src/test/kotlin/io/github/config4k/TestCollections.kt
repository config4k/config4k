package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.util.Arrays

class TestCollections : WordSpec({
    val config = "key = [0m, 1m, 1m, 2m]".toConfig()
    "Config.extract" should {
        "return List" {
            val list = config.extract<List<Duration>>("key")
            list shouldBe listOf(
                Duration.ofMinutes(0),
                Duration.ofMinutes(1),
                Duration.ofMinutes(1),
                Duration.ofMinutes(2)
            )
        }

        "return List<List<Int>>" {
            val intListConfig = "key = [[0, 0], [1, 1]]".toConfig()
            val list = intListConfig.extract<List<List<Int>>>("key")
            list shouldBe listOf(listOf(0, 0), listOf(1, 1))
        }

        "return Set" {
            val set = config.extract<Set<Duration>>("key")
            set should haveSize(3)
            set shouldBe setOf(
                Duration.ofMinutes(0),
                Duration.ofMinutes(1),
                Duration.ofMinutes(1),
                Duration.ofMinutes(2)
            )
        }

        "return Array<T>" {
            Arrays.deepEquals(
                """key = ["a", "b", "c", "d"]""".toConfig()
                    .extract<Array<String>>("key"),
                arrayOf("a", "b", "c", "d")
            ) shouldBe true
            Arrays.deepEquals(
                """key = ["0m", "1m"]""".toConfig()
                    .extract<Array<Duration>>("key"),
                arrayOf(
                    Duration.ofMinutes(0),
                    Duration.ofMinutes(1)
                )
            ) shouldBe true
        }
    }
})
