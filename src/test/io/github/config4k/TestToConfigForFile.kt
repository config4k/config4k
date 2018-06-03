package io.github.config4k

import io.kotlintest.specs.WordSpec
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths


class TestToConfigForFile : WordSpec() {
    init {
        "myFile.txt.toConfig" should {
            "return Config having relative File value" {
                File("myFile.txt").toConfig("key").extract<File>("key") shouldBe File("myFile.txt")
            }
            "return Config having relative Path value" {
                Paths.get("myFile.txt").toConfig("key").extract<Path>("key") shouldBe Paths.get("myFile.txt")
            }
        }

        "/tmp/myFile.txt.toConfig" should {
            "return Config having absolute File value" {
                File("/tmp/myFile.txt").toConfig("key").extract<File>("key") shouldBe File("/tmp/myFile.txt")
            }
            "return Config having absolute File value" {
                Paths.get("/tmp/myFile.txt").toConfig("key").extract<Path>("key") shouldBe Paths.get("/tmp/myFile.txt")
            }
        }
    }
}
