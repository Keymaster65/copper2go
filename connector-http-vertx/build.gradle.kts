pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.http.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("org.crac:crac:1.5.0")

    implementation("io.vertx:vertx-core:5.0.0")
    implementation("io.vertx:vertx-web:5.0.0")
    implementation("io.vertx:vertx-web-client:5.0.0")
}