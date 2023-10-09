pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.http.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:4.4.5")
    implementation("io.vertx:vertx-web:4.4.5")
    implementation("io.vertx:vertx-web-client:4.4.5")
}