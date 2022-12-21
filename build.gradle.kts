import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    java
    distribution
    `maven-publish`
    jacoco
    id("org.sonarqube") version "3.5.0.2730"
    id("com.github.jk1.dependency-license-report") version "2.1"
    id("com.google.cloud.tools.jib") version "3.3.1"
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("org.unbroken-dome.test-sets") version "4.0.0"
    id("org.owasp.dependencycheck") version "7.4.1"
    id("com.github.ben-manes.versions") version "0.44.0"
    id("info.solidsoft.pitest") version "1.9.11"
}

group = "io.github.keymaster65"

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

tasks.sonarqube {
    dependsOn(tasks.test)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.unbroken-dome.test-sets")
    apply(plugin = "org.sonarqube")
    apply(plugin = "jacoco")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "com.github.jk1.dependency-license-report")
    apply(plugin = "info.solidsoft.pitest")

    repositories {
        mavenCentral()
    }

    // https://docs.gradle.org/current/userguide/jacoco_plugin.html
    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report

        reports {
            xml.getRequired().set(true)
            csv.getRequired().set(false)
        }
    }

    // https://github.com/szpak/gradle-pitest-plugin
    pitest {
        junit5PluginVersion.set("1.0.0")
        timestampedReports.set(false)
    }

    // https://github.com/jk1/Gradle-License-Report
    licenseReport {
        filters = arrayOf<DependencyFilter>(
            LicenseBundleNormalizer(
                "$rootDir/license-normalizer-bundle.json",
                true
            )
        )
        // excludes and excludeOwnGroup not working
        excludeGroups  = arrayOf<String>("com.fasterxml.jackson", "io.github.keymaster65") // is apache 2.0 but license tool say "null" for jackson-bom v2.13.1
        allowedLicensesFile = File("$rootDir/allowed-licenses.json")
    }

    sonarqube {
        properties {
            property("sonar.projectKey", "Keymaster65_copper2go")
            property("sonar.organization", "keymaster65")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // see https://github.com/hierynomus/license-gradle-plugin
    apply(plugin = "com.github.hierynomus.license")
    license {
        setIgnoreFailures(false)
        setHeader(File("$rootDir/licenseHeader.txt"))
        setSkipExistingHeaders(false)
        exclude("**/*.json")
        exclude("**/test.html")
    }

    // https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html
    dependencyCheck {
        analyzers.assemblyEnabled = false
        failBuildOnCVSS = 0F
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    // https://github.com/ben-manes/gradle-versions-plugin
    fun isNonStable(version: String): Boolean {
        val nonStable = listOf("RC").any { version.toUpperCase().contains(it) }
        return nonStable
    }
    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
        outputFormatter = "plain,html"
    }

//    tasks.checkLicense {
//        dependsOn(tasks.processResources)
//    }
//    tasks.compileTestJava {
//        dependsOn(tasks.checkLicense)
//    }
//    tasks.compileTestJava {
//        dependsOn(tasks.generateLicenseReport)
//    }

//
//    tasks.withType<Test> {
//        dependsOn(tasks.checkLicense)
//    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.6")
        implementation("ch.qos.logback:logback-classic:1.4.5")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")

        testImplementation("org.assertj:assertj-assertions-generator:2.+")
        testImplementation("net.jqwik:jqwik:1.+")
        testImplementation("org.junit.jupiter:junit-jupiter:5.+")
        testImplementation("org.mockito:mockito-core:4.+")

        constraints {
            implementation("commons-io:commons-io:2.11.0") {
                because("Bug in 2.8.0 while deleting dirs on Windows 10; JDK11")
            }
            implementation("io.netty:netty-buffer:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-http:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-socks:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-common:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-handler:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-handler-proxy:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-resolver:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-transport:4.1.85.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-dns:4.1.85.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("io.netty:netty-codec-http2:4.1.85.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("io.netty:netty-resolver-dns:4.1.85.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("net.minidev:accessors-smart:2.4.8") {
                because("Security scan found 1.2")
            }
            implementation("org.apache.httpcomponents:httpclient:4.5.14") {
                because("Security scan found 4.5.2")
            }
            implementation("net.minidev:json-smart:2.4.8") {
                because("Security scan found 2.3")
            }
            implementation("org.apache.velocity:velocity-engine-core:2.3") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.velocity:velocity-engine-scripting:2.3") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.kafka:kafka-clients:3.3.1")

            implementation("com.google.guava:guava:31.1-jre") {
                because("Security scan found 23.4-jre. Needed for assertj and copper.")
            }

        }
    }

    // https://github.com/unbroken-dome/gradle-testsets-plugin
    testSets {
        create("integrationTest")
        create("systemTest")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines.add("junit-jupiter")
            includeEngines.add("jqwik")
        }
        systemProperty("logback.configurationFile", "src/main/resources/logback.xml")
    }
}

distributions {
    main {
        contents {
            into("config") {
                from("src/main/resources/logback.xml")
            }
        }
    }
}