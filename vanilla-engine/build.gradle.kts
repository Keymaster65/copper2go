pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.vanilla.*"))
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine-api"))
}