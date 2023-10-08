pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.clientsimulator.*"))
}

plugins {
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

tasks.jar {
    dependsOn(tasks.findByName("checkLicense"))
}

dependencies {
    implementation("io.dropwizard.metrics:metrics-core:4.2.20")
    implementation("io.dropwizard.metrics:metrics-jmx:4.2.20")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.clientsimulator.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dlogback.configurationFile=src/main/resources/logback.xml",
        "-Dcom.sun.management.jmxremote.port=10081",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false",
        "--enable-preview"
    )
}
tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    jvmArgs = listOf("--enable-preview")
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