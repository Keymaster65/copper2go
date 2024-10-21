import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    java
    distribution
    `maven-publish`
    jacoco
    id("org.sonarqube") version "5.1.0.4882"
    id("com.github.jk1.dependency-license-report") version "2.9"
    id("com.google.cloud.tools.jib") version "3.4.4" // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("org.unbroken-dome.test-sets") version "4.1.0"
    id("org.owasp.dependencycheck") version "10.0.4"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("info.solidsoft.pitest") version "1.15.0"
}

apply(plugin = "info.solidsoft.pitest.aggregator")

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
        maven(
            url = "https://oss.sonatype.org/content/groups/staging",
        )
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
        // check this in case of errors: https://mvnrepository.com/artifact/org.pitest/pitest-junit5-plugin
        junit5PluginVersion.set("1.2.1")
        timestampedReports.set(false)
        outputFormats.set(setOf("HTML","XML"))
        exportLineCoverage.set(true)
        verbose = true
        addJUnitPlatformLauncher = false

        reportAggregator {
            testStrengthThreshold.set(77)
            mutationThreshold.set(75)
            maxSurviving.set(105)
        }
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
        excludeGroups = arrayOf<String>(
            "com.fasterxml.jackson",
            "io.github.keymaster65"
        ) // is apache 2.0 but license tool say "null" for jackson-bom v2.13.1
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
        toolchain {
            languageVersion = JavaLanguageVersion.of(22)
        }
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

    val nvdApiKey : String = project.properties.getValue("nvdApiKey") as String

    // https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/index.html
    dependencyCheck {
        analyzers.assemblyEnabled = false
        failBuildOnCVSS = 0F
        suppressionFile = "./cveSuppressionFile.xml"
        nvd.apiKey = nvdApiKey
        nvd.delay = 4000
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    // https://github.com/ben-manes/gradle-versions-plugin
    fun isNonStable(version: String): Boolean {
        val nonStable = listOf("-ALPHA", "-RC").any { version.uppercase().contains(it) }
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
        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation("ch.qos.logback:logback-classic:1.5.11")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")

        testImplementation("org.assertj:assertj-assertions-generator:2.2.1")
        testImplementation("net.jqwik:jqwik:1.9.1")
        testImplementation("org.junit.jupiter:junit-jupiter:5.11.2")
        testImplementation("org.mockito:mockito-core:5.14.2")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
            because("required for pitest")
        }

        constraints {
            implementation("commons-io:commons-io:2.17.0") {
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
            implementation("org.apache.velocity:velocity-engine-core:2.4") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.velocity:velocity-engine-scripting:2.4.1") {
                because("Security scan found 2.2")
            }
            implementation("org.apache.kafka:kafka-clients:3.8.0")

            implementation("com.google.guava:guava:33.3.1-jre") {
                because("Security scan found 31.1-jre. Needed for assertj and copper.")
            }
            implementation("org.xerial.snappy:snappy-java:1.1.10.7")
            implementation("io.netty:netty-handler:4.1.114.Final")

            pitest("org.pitest:pitest-command-line:1.17.0")
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
