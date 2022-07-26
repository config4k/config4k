import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.Duration

group = "io.github.config4k"
version = "0.5.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version "1.4.20"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `maven-publish`
    signing
    jacoco
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api("com.typesafe", "config", "1.3.3")
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "4.4.3")
    testImplementation("org.commonmark", "commonmark", "0.17.1")
    testImplementation(kotlin("script-util"))
    testImplementation(kotlin("compiler-embeddable"))
    testImplementation(kotlin("scripting-compiler-embeddable"))
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
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

nexusPublishing {
    packageGroup.set(project.group.toString())
    clientTimeout.set(Duration.ofMinutes(60))
    repositories {
        sonatype()
    }
}

ktlint {
    version.set("0.37.2")
    outputToConsole.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
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
    useGpgCmd()
    sign(publishing.publications["maven"])
}
