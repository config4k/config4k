import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.Duration

group = "io.github.config4k"
version = "0.5.0-SNAPSHOT"

val ossrhUsername: String? by project
val ossrhPassword: String? by project

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version "1.3.10"
    id("org.jetbrains.dokka") version "0.10.1"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
    id("io.codearte.nexus-staging") version "0.21.2"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    signing
    jacoco
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api("com.typesafe", "config", "1.3.3")
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "4.1.0")
    testImplementation("com.atlassian.commonmark", "commonmark", "0.13.1")
    testImplementation(kotlin("script-util"))
    testImplementation(kotlin("compiler-embeddable"))
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokka)
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.destination = file("$buildDir/jacocoHtml")
    }
    dependsOn(tasks.test)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.dokka {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
    configuration {
        jdkVersion = 8
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

nexusPublishing {
    repositories {
        sonatype()
    }
    clientTimeout.set(Duration.parse("PT10M")) // 10 minutes
}

nexusStaging {
    packageGroup = project.group.toString()
    username = ossrhUsername
    password = ossrhPassword
    numberOfRetries = 360 // 1 hour if 10 seconds delay
    delayBetweenRetriesInMillis = 10000 // 10 seconds
}

ktlint {
    version.set("0.37.2")
    outputToConsole.set(true)
    reporters.set(setOf(ReporterType.CHECKSTYLE))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            pom {
                name.set("A Typesafe Config wrapper for Kotlin")
                description.set("A Typesafe Config wrapper for Kotlin")
                url.set("https://github.com/config4k/config4k")
                organization {
                    name.set("Config4k")
                    url.set("https://github.com/config4k/config4k")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("config4k")
                        name.set("Config4k Contributors")
                        email.set("config4k@config4k.github.io")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/config4k/config4k.git")
                    developerConnection.set("scm:git:https://github.com/config4k/config4k.git")
                    url.set("https://github.com/config4k/config4k")
                    tag.set("HEAD")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
