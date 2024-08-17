plugins {
    id("org.unbroken-dome.test-sets") version "4.1.0"
    id("info.solidsoft.pitest") version "1.15.0"
    java
}

apply(plugin = "info.solidsoft.pitest.aggregator")

group = "io.github.keymaster65"

allprojects {
    apply(plugin = "org.unbroken-dome.test-sets")
    apply(plugin = "info.solidsoft.pitest")
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven(
            url = "https://oss.sonatype.org/content/groups/staging",
        )
    }

    // https://github.com/szpak/gradle-pitest-plugin
    pitest {
        // check this in case of errors: https://mvnrepository.com/artifact/org.pitest/pitest-junit5-plugin
        junit5PluginVersion.set("1.2.1")
        timestampedReports.set(false)
        outputFormats.set(setOf("HTML", "XML"))
        exportLineCoverage.set(true)
        verbose = true

        reportAggregator {
            testStrengthThreshold.set(77)
            mutationThreshold.set(75)
            maxSurviving.set(105)
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(22)
        }
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation("ch.qos.logback:logback-classic:1.5.7")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

        testImplementation("org.assertj:assertj-assertions-generator:2.2.1")
        testImplementation("net.jqwik:jqwik:1.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
        testImplementation("org.mockito:mockito-core:5.12.0")

        constraints {
            implementation("commons-io:commons-io:2.16.1") {
                because("Bug in 2.8.0 while deleting dirs on Windows 10; JDK11")
            }
            implementation("net.minidev:accessors-smart:2.5.1") {
                because("Security scan found 1.2")
            }
            implementation("org.apache.httpcomponents:httpclient:4.5.14") {
                because("Security scan found 4.5.2")
            }
            implementation("net.minidev:json-smart:2.5.1") {
                because("Security scan found 2.3")
            }
            implementation("org.apache.velocity:velocity-engine-core:2.3") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.velocity:velocity-engine-scripting:2.3") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.kafka:kafka-clients:3.8.0")

            implementation("com.google.guava:guava:33.2.1-jre") {
                because("Security scan found 31.1-jre. Needed for assertj and copper.")
            }
            implementation("org.xerial.snappy:snappy-java:1.1.10.5")
            implementation("io.netty:netty-handler:4.1.112.Final")

            pitest("org.pitest:pitest-command-line:1.16.1")
        }
    }

    // https://github.com/unbroken-dome/gradle-testsets-plugin
    testSets {
        create("integrationTest")
        create("systemTest")
    }
}