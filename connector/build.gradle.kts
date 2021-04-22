dependencies {
    implementation(project(":engine"))
    implementation(project(":copper2go-api"))

    implementation("io.vertx:vertx-core:4+")
    implementation("io.vertx:vertx-web:4+")
    implementation("io.vertx:vertx-web-client:4+")
    //implementation("io.vertx:web-examples:3.8.5")

    implementation("com.google.guava:guava:30.1.1-jre")
}