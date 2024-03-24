pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.scotty.*"))
}

tasks.withType<Test> {
    jvmArgs = listOf("-Dorg.copperengine.workflow.compiler.options=-target,21,-source,21")
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.2")
    implementation("org.copper-engine:copper-jmx-interface:5.4.2")
    implementation("org.copper-engine:copper-ext:5.4.2") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

    // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")

    implementation("org.crac:crac:1.4.0")
}