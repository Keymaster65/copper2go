pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.pricingsimulator.*"))
}

plugins {
    application
}

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
    implementation("io.dropwizard.metrics:metrics-core:4.2.23")
    implementation("io.dropwizard.metrics:metrics-jmx:4.2.23")
}

application {
    mainClass.set("io.github.keymaster65.copper2go.pricingsimulator.Main")
    applicationDefaultJvmArgs = listOf(
        "-Dlogback.configurationFile=src/main/resources/logback.xml",
        "-Dcom.sun.management.jmxremote.port=10080",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false"
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