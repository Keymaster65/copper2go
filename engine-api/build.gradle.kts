pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.*"))
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.2")
    implementation("org.copper-engine:copper-jmx-interface:5.4.2")
    implementation("org.copper-engine:copper-ext:5.4.2") {
        exclude(group = "org.yaml", module ="snakeyaml" )
    }

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")
}