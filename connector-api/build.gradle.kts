pitest {
  targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.api.connector.*"))
}

dependencies {
    implementation(project(":copper2go-api"))
}