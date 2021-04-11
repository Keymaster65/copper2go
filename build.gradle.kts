import com.github.jk1.license.filter.LicenseBundleNormalizer

plugins {
    java
    application
    distribution
    `maven-publish`
    jacoco
    id("com.github.jk1.dependency-license-report") version "1.16"
    id("com.google.cloud.tools.jib") version "3.0.0"
}

group = "de.wolfsvl"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencyLocking {
    lockAllConfigurations()
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
    }
}

task<JacocoMerge>("jacocoMerge") {
    destinationFile = File("$buildDir/jacoco/allTestCoverage.exec")
    executionData = fileTree("$buildDir/jacoco")
}

tasks.withType<JacocoReport> {
    reports {
        executionData.setFrom("$buildDir/jacoco/allTestCoverage.exec")
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}
dependencies {

    implementation("com.fasterxml.jackson.core:jackson-databind:2+")

    implementation("io.vertx:vertx-core:4+")
    implementation("io.vertx:vertx-web:4+")
    implementation("io.vertx:vertx-web-client:4+")
    //implementation("io.vertx:web-examples:3.8.5")

    implementation("org.copper-engine:copper-ext:5+")

    // needed for exceptions
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")

    implementation("org.copper-engine:copper-coreengine:5+")
    implementation("org.copper-engine:copper-jmx-interface:5+")

    implementation("org.slf4j:slf4j-api:1.8.0-beta2")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")

    testImplementation("org.assertj:assertj-assertions-generator:2+")
    testImplementation("org.junit.jupiter:junit-jupiter:5+")
    testImplementation("org.mockito:mockito-core:3+")
}

application {
    mainClassName = "de.wolfsvl.copper2go.Main"
    applicationDefaultJvmArgs = listOf("-Dlogback.configurationFile=src/main/resources/logback.xml")
}

distributions {
    main {
        contents {
            into("") {
                from("$buildDir/reports/dependency-license/index.html", "LICENSE")
                rename {
                    it.replace(
                            "index.html",
                            "licence.html"
                    )
                }
            }
            into("config") {
                from("src/main/resources/logback.xml")
            }
        }
    }
}

// visit https://github.com/jk1/Gradle-License-Report for help
licenseReport {
    excludeOwnGroup = false
    allowedLicensesFile = File("$projectDir/allowed-licenses.json")
    excludes = arrayOf<String>("com.fasterxml.jackson:jackson-bom") // is apache 2.0 but license tool say "null"
    filters = arrayOf<LicenseBundleNormalizer>(LicenseBundleNormalizer("""$projectDir/license-normalizer-bundle.json""", true))
}

jib {
    container {
        mainClass = "de.wolfsvl.copper2go.Main"
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
        image = "azul/zulu-openjdk-alpine:11.0.7"
    }
    to {
        image = "registry.hub.docker.com/keymaster65/copper2go"
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
        tags = setOf("latest", "v1.0")
    }
    extraDirectories {
        paths {
            path {
                setFrom(project.projectDir.toPath().resolve("build").resolve("reports").resolve("dependency-license"))
                into = "/app/resources/license"
            }
        }
    }
}