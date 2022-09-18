plugins {
    application
    id("com.google.cloud.tools.jib") version "3.3.0"
}

var copper2goVersion = "vanilla-tmp"

dependencies {
    implementation(project(":application-framework"))

    implementation(project(":copper2go-api"))
    implementation(project(":engine-api"))
    implementation(project(":connector-api"))

    implementation(project(":vanilla-engine"))

    implementation(project(":connector-http-vertx"))
    implementation("io.vertx:vertx-core:4.3.+")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.vanilla.application.Main")
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
        mainClass = "io.github.keymaster65.copper2go.vanilla.application.Main"
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