pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.*"))
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.1")
    implementation("org.copper-engine:copper-jmx-interface:5.4.1")
    implementation("org.copper-engine:copper-ext:5.4.1") {
        exclude(group = "org.yaml", module ="snakeyaml" )
    }

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.4.0.202211300538-r")
}