pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.sync.application.*"))
}

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.3.1"
}

var copper2goVersion = "sync-tmp"

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
    implementation("io.dropwizard.metrics:metrics-core:4.2.15")
    implementation("io.dropwizard.metrics:metrics-jmx:4.2.15")
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
            "-Duser.timezone=Europe/Berlin",
            "--enable-preview"
        )
        workingDirectory = "/"
    }
    from {
        image = "openjdk:19-jdk"
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