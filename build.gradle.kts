import com.github.jk1.license.filter.LicenseBundleNormalizer

plugins {
    java
    application
    distribution
    `maven-publish`
    jacoco
    id("org.sonarqube") version "3.4.0.2513"
    id("com.github.jk1.dependency-license-report") version "2.1"
    id("com.google.cloud.tools.jib") version "3.3.0"
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("org.unbroken-dome.test-sets") version "4.0.0"
    id("org.owasp.dependencycheck") version "7.1.2"
    id("com.github.ben-manes.versions") version "0.42.0"
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine"))
    implementation(project(":connector-http-vertx"))
    implementation(project(":connector-kafka-vertx"))
    implementation(project(":connector-standardio"))

    implementation("org.copper-engine:copper-coreengine:5.4.1")

    configurations.implementation {
        // due to license issue and I guess I currently do not need it
        exclude("io.netty", "netty-tcnative-classes")
    }
    implementation("io.vertx:vertx-core:4.3.+")
    implementation("io.vertx:vertx-kafka-client:4.3.+")

    testImplementation("org.testcontainers:testcontainers:1.+")
    testImplementation("org.testcontainers:kafka:1.+")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.Main")
    applicationDefaultJvmArgs = listOf("-Dlogback.configurationFile=src/main/resources/logback.xml")
}

group = "io.github.keymaster65"
version = "4.1"

tasks.sonarqube {
    dependsOn(tasks.test)
}

tasks.checkLicense {
    dependsOn(tasks.findByName("processResources"))
}

tasks.jar {
    dependsOn(tasks.findByName("checkLicense"))
}
tasks.compileTestJava {
    dependsOn(tasks.findByName("checkLicense"))
}

var ct = tasks.checkLicense

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.unbroken-dome.test-sets")
    apply(plugin = "org.sonarqube")
    apply(plugin = "jacoco")
    apply(plugin = "org.owasp.dependencycheck")

    // https://docs.gradle.org/current/userguide/jacoco_plugin.html
    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report

        reports {
            xml.getRequired().set(true)
            csv.getRequired().set(false)
            //html.destination = layout.buildDirectory.dir("jacocoHtml").get().asFile
        }
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
        setHeader(File("$rootDir/licenceHeader.txt"))
        setSkipExistingHeaders(false)
        exclude("**/*.json")
        exclude("**/test.html")
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.0")
        implementation("ch.qos.logback:logback-classic:1.4.0")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")

        testImplementation("org.assertj:assertj-assertions-generator:2.+")
        testImplementation("net.jqwik:jqwik:1.+")
        testImplementation("org.junit.jupiter:junit-jupiter:5.+")
        testImplementation("org.mockito:mockito-core:4.+")

        constraints {
            implementation("commons-io:commons-io:2.11.0") {
                because("Bug in 2.8.0 while deleting dirs on Windows 10; JDK11")
            }
            implementation("com.google.guava:guava:31.1-jre") {
                because("Security scan found 23.4-jre")
            }
            implementation("io.netty:netty-buffer:4.1.80.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec:4.1.80.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-http:4.1.81.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-socks:4.1.81.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-common:4.1.80.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-handler:4.1.80.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-handler-proxy:4.1.81.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-resolver:4.1.80.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-transport:4.1.81.Final") {
                because("Security scan found 4.1.53.Final")
            }
            implementation("io.netty:netty-codec-dns:4.1.80.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("io.netty:netty-codec-http2:4.1.80.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("io.netty:netty-resolver-dns:4.1.80.Final") {
                because("Security scan found 4.1.74.Final")
            }
            implementation("net.minidev:accessors-smart:2.4.8") {
                because("Security scan found 1.2")
            }
            implementation("org.apache.httpcomponents:httpclient:4.5.13") {
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
            implementation("org.apache.kafka:kafka-clients:3.2.1")
        }
    }

    testSets {
        create("integrationTest")
        create("systemTest")
    }

    tasks.check {
        dependsOn(tasks.findByName("systemTest"))
    }

    tasks.check {
        dependsOn(tasks.findByName("integrationTest"))
    }

    tasks.findByName("compileSystemTestJava")?.dependsOn(ct)
    tasks.findByName("compileIntegrationTestJava")?.dependsOn(ct)

    dependencyLocking {
        lockAllConfigurations()
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines.add("junit-jupiter")
            includeEngines.add("jqwik")
        }
        systemProperty("logback.configurationFile", "src/main/resources/logback.xml")
    }
}


// visit https://github.com/jk1/Gradle-License-Report for help
licenseReport {
    outputDir = "$projectDir/build/resources/main/license"
    excludeOwnGroup = true
    allowedLicensesFile = File("$projectDir/allowed-licenses.json")
    excludes = arrayOf<String>("com.fasterxml.jackson:jackson-bom") // is apache 2.0 but license tool say "null"
    filters = arrayOf<LicenseBundleNormalizer>(
        LicenseBundleNormalizer(
            """$projectDir/license-normalizer-bundle.json""",
            true
        )
    )
}

tasks.assemble {
    dependsOn(tasks.findByName("checkLicense"))
}

tasks.jib {
    dependsOn(tasks.findByName("checkLicense"))
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

jib {
    container {
        mainClass = "io.github.keymaster65.copper2go.Main"
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-Dfile.encoding=UTF-8",
            "-Duser.country=DE",
            "-Duser.language=de",
            "-Duser.timezone=Europe/Berlin"
        )
        workingDirectory = "/"
    }
    from {
        // Has no bash, needed for testcontainers
        // image = "azul/zulu-openjdk-alpine:17.0.0"
        image = "openjdk:17-jdk"
    }
    to {
        image = "registry.hub.docker.com/keymaster65/copper2go"
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
        tags = setOf("latest")
    }
}