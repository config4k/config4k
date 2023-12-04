package io.github.config4k

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.time.Duration

class TestCollections : WordSpec({
    val config = "key = [0m, 1m, 1m, 2m]".toConfig()
    "Config.extract" should {
        "return List" {
            val list = config.extract<List<Duration>>("key")
            list shouldBe
                listOf(
                    Duration.ofMinutes(0),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(2),
                )
        }

        "return MutableList" {
            val list = config.extract<MutableList<Duration>>("key")
            list shouldBe
                listOf(
                    Duration.ofMinutes(0),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(2),
                )
            list.add(Duration.ofMinutes(10))
            list shouldBe
                listOf(
                    Duration.ofMinutes(0),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(2),
                    Duration.ofMinutes(10),
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
            set shouldBe
                setOf(
                    Duration.ofMinutes(0),
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(2),
                )
        }

        "return MutableSet" {
            val setConfig = "key = [1m]".toConfig()
            val set = setConfig.extract<MutableSet<Duration>>("key")
            set should haveSize(1)
            set shouldBe setOf(Duration.ofMinutes(1))
            set.add(Duration.ofMinutes(10))
            set should haveSize(2)
            set shouldBe
                setOf(
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(10),
                )
        }

        "return Array<T>" {
            """key = ["a", "b", "c", "d"]""".toConfig()
                .extract<Array<String>>("key")
                .contentDeepEquals(
                    arrayOf("a", "b", "c", "d"),
                ) shouldBe true

            """key = ["0m", "1m"]""".toConfig()
                .extract<Array<Duration>>("key")
                .contentDeepEquals(
                    arrayOf(
                        Duration.ofMinutes(0),
                        Duration.ofMinutes(1),
                    ),
                ) shouldBe true
        }
    }
})
