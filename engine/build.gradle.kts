dependencies {
    implementation(project(":copper2go-api"))

    implementation("org.copper-engine:copper-ext:5+")
    implementation("org.copper-engine:copper-coreengine:5+")
    implementation("org.copper-engine:copper-jmx-interface:5+")

    // needed for exceptions
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")

}