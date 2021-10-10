dependencies {
    implementation(project(":copper2go-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.0")
    implementation("org.copper-engine:copper-jmx-interface:5.4.0")
    implementation("org.copper-engine:copper-ext:5.4.0")

    // needed for exceptions
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.12.0.202106070339-r")

}