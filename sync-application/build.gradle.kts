pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.sync.application.*"))
}

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.4.2"
}

var copper2goVersion = "sync-0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    dependsOn(tasks.findByName("checkLicense"))
}

dependencies {
    implementation(project(":application-framework"))
    implementation(project(":sync-engine"))
}

application {
    mainClass.set("io.github.keymaster65.copper2go.sync.application.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dlogback.configurationFile=src/main/resources/logback.xml"
    )
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
        mainClass = "io.github.keymaster65.copper2go.sync.application.Main"
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
        image = "openjdk:21-jdk"
    }
    to {
        image = "registry.hub.docker.com/keymaster65/copper2go:" + copper2goVersion
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
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

tasks.withType<Test> {
    systemProperty("copper2go.version", copper2goVersion)
}