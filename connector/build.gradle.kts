dependencies {
    implementation(project(":engine"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:4.1.+")
    implementation("io.vertx:vertx-web:4.1.+")
    implementation("io.vertx:vertx-web-client:4.0.+")

    implementation("io.vertx:vertx-kafka-client:4.1.+")

    implementation("com.google.guava:guava:30.+")

    testImplementation("org.testcontainers:kafka:1.+")
}