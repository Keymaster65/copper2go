dependencies {
    implementation(project(":copper2go-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.1")
    implementation("org.copper-engine:copper-jmx-interface:5.4.1")
    implementation("org.copper-engine:copper-ext:5.4.1")

    constraints {
        implementation("commons-io:commons-io:2.11.0") {
            because("Bug in 2.8.0 while deleting dirs on Windows 10; JDK11")
        }
    }

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.0.0.202111291000-r")

}