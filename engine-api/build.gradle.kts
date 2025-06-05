pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.*"))
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))

    implementation("org.copper-engine:copper-coreengine:5.5.2")
    implementation("org.copper-engine:copper-jmx-interface:5.5.2")
    implementation("org.copper-engine:copper-ext:5.5.2") {
        exclude(group = "org.yaml", module ="snakeyaml" )
    }

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.2.1.202505142326-r")
}