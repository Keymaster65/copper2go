pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.kafka.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:5.0.0")
    implementation("io.vertx:vertx-web:5.0.0")
    implementation("io.vertx:vertx-kafka-client:5.0.0")

    testImplementation("org.testcontainers:kafka:1.21.1")
    constraints {
        testImplementation("org.apache.commons:commons-compress:1.27.1")
    }
}