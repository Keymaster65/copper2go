pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.http.vertx.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))

    implementation("io.github.crac:org-crac:0.1.3")

    implementation("io.vertx:vertx-core:4.4.6")
    implementation("io.vertx:vertx-web:4.4.6")
    implementation("io.vertx:vertx-web-client:4.4.6")
}