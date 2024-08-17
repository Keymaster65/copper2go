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

    // https://github.com/unbroken-dome/gradle-testsets-plugin
    testSets {
        create("integrationTest")
        create("systemTest")
    }
}