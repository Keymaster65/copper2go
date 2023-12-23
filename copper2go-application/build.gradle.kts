pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.*"))
}

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.4.0"
}

tasks.withType<Test> {
    jvmArgs = listOf("-Dorg.copperengine.workflow.compiler.options=-target,21,-source,21")
}

val copper2goVersion : String = project.property("copper2goVersion").toString()

tasks.jar {
    dependsOn(tasks.findByName("checkLicense"))
}

dependencies {
    implementation(project(":application-framework"))

    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine-api"))

    implementation(project(":scotty-engine"))

    implementation(project(":connector-http-vertx"))
    implementation(project(":connector-kafka-vertx"))
    implementation(project(":connector-standardio"))

    implementation("org.copper-engine:copper-coreengine:5.4.2")

    implementation("io.vertx:vertx-core:4.5.1")
    implementation("io.vertx:vertx-kafka-client:4.5.1")

    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:kafka:1.19.3")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.Main")
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
    from {
        image = "azul/zulu-openjdk-alpine:21.0.0"
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
    }
    to {
        image = "registry.hub.docker.com/keymaster65/copper2go:" + copper2goVersion
        auth {
            username = "keymaster65"
            password = System.getenv("DOCKER_HUB_PASSWORD")
        }
    }
    container {
        mainClass = "io.github.keymaster65.copper2go.Main"
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-Dfile.encoding=UTF-8",
            "-Duser.country=DE",
            "-Duser.language=de",
            "-Duser.timezone=Europe/Berlin",
        )
        workingDirectory = "/app"
        user = "games"
    }
    extraDirectories {
        paths {
            path {
                setFrom(project.projectDir.toPath().resolve("build").resolve("reports").resolve("dependency-license"))
                into = "/app/resources/license"
            }
            path {
                setFrom(project.projectDir.toPath().resolve("src").resolve("main").resolve("jib").resolve("app"))
                excludes.set(listOf("**/.gitkeep"))
                into = "/app"
            }
            path {
                setFrom(project.projectDir.toPath().resolve("src").resolve("main").resolve("jib").resolve("home"))
                excludes.set(listOf("**/.gitkeep"))
                into = "/usr/games"
            }
        }
        permissions.set(
            mapOf(
                "/usr/games/.config" to "777",
                "/app/.copper" to "777"
            )
        )
    }
}

tasks.withType<Test> {
    systemProperty("copper2go.version", copper2goVersion)
}