dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.1")
    implementation("org.copper-engine:copper-jmx-interface:5.4.1")
    implementation("org.copper-engine:copper-ext:5.4.1")

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.0.0.202111291000-r")

}