pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.kafka.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:4.5.4")
    implementation("io.vertx:vertx-web:4.5.4")
    implementation("io.vertx:vertx-kafka-client:4.5.4")

    testImplementation("org.testcontainers:kafka:1.19.4")
}