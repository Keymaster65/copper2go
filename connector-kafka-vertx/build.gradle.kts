pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.kafka.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    configurations.implementation {
        // due to license issue and I guess I currently do not need it
        exclude("io.netty","netty-tcnative-classes")
    }

    implementation("io.vertx:vertx-core:4.4.1")
    implementation("io.vertx:vertx-web:4.4.1")
    implementation("io.vertx:vertx-kafka-client:4.4.1")

    testImplementation("org.testcontainers:kafka:1.17.6")
}