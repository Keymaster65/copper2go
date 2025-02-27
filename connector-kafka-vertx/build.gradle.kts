pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.kafka.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:4.5.13")
    implementation("io.vertx:vertx-web:4.5.13")
    implementation("io.vertx:vertx-kafka-client:4.5.13")

    testImplementation("org.testcontainers:kafka:1.20.5")
    constraints {
        testImplementation("org.apache.commons:commons-compress:1.27.1")
    }
}