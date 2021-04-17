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




publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}
dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":engine"))
    implementation(project(":connector"))

    implementation("org.copper-engine:copper-coreengine:5+")

    implementation("io.vertx:vertx-core:4+")
}

application {
    mainClassName = "de.wolfsvl.copper2go.Main"
    applicationDefaultJvmArgs = listOf("-Dlogback.configurationFile=src/main/resources/logback.xml")
}

group = "de.wolfsvl"
version = "2.0"

allprojects {
    apply(plugin= "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    dependencies{
        implementation("org.slf4j:slf4j-api:1.8.0-beta2")
        implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")

        implementation("com.fasterxml.jackson.core:jackson-databind:2+")

        testImplementation("org.assertj:assertj-assertions-generator:2+")
        testImplementation("org.junit.jupiter:junit-jupiter:5+")
        testImplementation("org.mockito:mockito-core:3+")
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

}

// visit https://github.com/jk1/Gradle-License-Report for help
licenseReport {
    excludeOwnGroup = true
    allowedLicensesFile = File("$projectDir/allowed-licenses.json")
    excludes = arrayOf<String>("com.fasterxml.jackson:jackson-bom") // is apache 2.0 but license tool say "null"
    filters = arrayOf<LicenseBundleNormalizer>(LicenseBundleNormalizer("""$projectDir/license-normalizer-bundle.json""", true))
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
        tags = setOf("latest")
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