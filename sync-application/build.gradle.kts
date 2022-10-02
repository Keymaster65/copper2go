pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.sync.application.*"))
}

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.3.0"
}

var copper2goVersion = "sync-0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

tasks.jar {
    dependsOn(tasks.findByName("checkLicense"))
}

dependencies {
    implementation(project(":application-framework"))
    implementation(project(":sync-engine"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    jvmArgs = listOf("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("--enable-preview")

}

application {
    mainClass.set("io.github.keymaster65.copper2go.sync.application.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dlogback.configurationFile=src/main/resources/logback.xml",
        "--enable-preview"
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
        // Has no bash, needed for testcontainers
        // image = "azul/zulu-openjdk-alpine:17.0.0"
        image = "openjdk:17-jdk"
    }
    to {
        image = "registry.hub.docker.com/keymaster65/copper2go:" + copper2goVersion
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
    }
}

tasks.withType<Test> {
    systemProperty("copper2go.version", copper2goVersion)
}