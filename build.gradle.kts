import com.github.jk1.license.filter.LicenseBundleNormalizer

plugins {
    java
    application
    distribution
    `maven-publish`
    jacoco
    id("org.sonarqube") version "3.2.0"
    id("com.github.jk1.dependency-license-report") version "1.16"
    id("com.google.cloud.tools.jib") version "3.1.1"
    id("com.github.hierynomus.license-base") version "0.16.1"
    id("org.unbroken-dome.test-sets") version "4.0.0"
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

    implementation("org.copper-engine:copper-coreengine:5.3.0")

    implementation("io.vertx:vertx-core:4.1.+")
    implementation("io.vertx:vertx-kafka-client:4.1.+")

    testImplementation("org.testcontainers:testcontainers:1.+")
    testImplementation("org.testcontainers:kafka:1.+")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.Main")
    applicationDefaultJvmArgs = listOf("-Dlogback.configurationFile=src/main/resources/logback.xml")
}

group = "io.github.keymaster65"
version = "2.2"

testSets {
    create("systemTest")
}

tasks.check {
    dependsOn(tasks.findByName("systemTest"))
}

// https://docs.gradle.org/current/userguide/jacoco_plugin.html
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.sonarqube {
    dependsOn(tasks.test)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.unbroken-dome.test-sets")
    apply(plugin = "org.sonarqube")
    apply(plugin = "jacoco")



    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
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
        sourceCompatibility = JavaVersion.VERSION_11
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }


    // see https://github.com/hierynomus/license-gradle-plugin
    apply(plugin = "com.github.hierynomus.license")
    license {
        setIgnoreFailures(false)
        setHeader(File("$rootDir/licenceHeader.txt"))
        setSkipExistingHeaders(false)
        exclude("**/*.json")
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
        implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")

        implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

        testImplementation("org.assertj:assertj-assertions-generator:2.+")
        testImplementation("org.junit.jupiter:junit-jupiter:5.+")
        testImplementation("org.mockito:mockito-core:3.+")
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    repositories {
        mavenCentral()
    }

    testSets {
        create("integrationTest")
    }

    tasks.check {
        dependsOn(tasks.findByName("integrationTest"))
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines.add("junit-jupiter")
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
    filters = arrayOf<LicenseBundleNormalizer>(LicenseBundleNormalizer("""$projectDir/license-normalizer-bundle.json""", true))
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
}