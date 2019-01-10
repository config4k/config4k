import com.novoda.gradle.release.PublishExtension
import org.jetbrains.dokka.gradle.DokkaTask

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.novoda:bintray-release:0.8.0")
    }
}

plugins {
    `java-library`
    kotlin("jvm") version "1.3.10"
    jacoco
    id("org.jetbrains.dokka") version "0.9.17"
}

apply(plugin = "com.novoda.bintray-release")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    api("com.typesafe:config:1.3.3")
    implementation("com.google.guava:guava:26.0-jre")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.8")
}

tasks {
    test {
        useJUnitPlatform()
    }

    jacocoTestReport {
        reports {
            html.isEnabled = false
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }

    val dokkaJavadoc = task<DokkaTask>("dokkaJavadoc") {
        outputFormat = "html"
        outputDirectory = "$buildDir/tmp/kdoc"
    }

    val javadocJar = task<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveFileName.set("config4k-javadoc.jar")
        from("$buildDir/tmp/kdoc")
    }

    afterEvaluate {
        val mavenSourcesJar by tasks
        mavenSourcesJar.dependsOn(javadocJar)
    }
}

configure<PublishExtension> {
    userOrg = "config4k"
    repoName = "config4k"
    groupId = "io.github.config4k"
    artifactId = "config4k"
    publishVersion = "0.4.1"
    description = "A Typesafe Config wrapper for Kotlin"
    website = "https://github.com/config4k/config4k"
    setLicences("Apache-2.0")
    issueTracker = "https://github.com/config4k/config4k/issues"
    repository = "https://github.com/config4k/config4k.git"
}
