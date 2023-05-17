import fr.brouillard.oss.jgitver.Strategies.MAVEN
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.time.Duration

group = "io.github.config4k"

repositories {
    mavenCentral()
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.jgitver)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
    jacoco
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    api(libs.typesafe.config)
    testImplementation(libs.kotest)
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

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.allWarningsAsErrors = true
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.allWarningsAsErrors = false
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true) // For Codecov
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
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

kotlin {
    explicitApi()
}

nexusPublishing {
    packageGroup.set(project.group.toString())
    clientTimeout.set(Duration.ofMinutes(60))
    repositories {
        sonatype()
    }
}

ktlint {
    version.set(libs.versions.ktlint.get())
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
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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

jgitver {
    strategy(MAVEN)
    nonQualifierBranches = "main"
}
