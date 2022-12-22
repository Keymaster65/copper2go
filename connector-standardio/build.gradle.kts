pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.connector.standardio.*"))
}

dependencies {
    implementation(project(":connector-api"))
    implementation(project(":copper2go-api"))
}