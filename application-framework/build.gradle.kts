pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.application.*"))
}
dependencies {
    implementation(project(":connector-api"))
}