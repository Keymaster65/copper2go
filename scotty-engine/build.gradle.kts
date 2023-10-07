pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.engine.scotty.*"))
}

tasks.withType<Test> {
    jvmArgs = listOf("-Dorg.copperengine.workflow.compiler.options=-target,17,-source,17")
}

dependencies {
    implementation(project(":copper2go-api"))
    implementation(project(":connector-api"))
    implementation(project(":engine-api"))

    implementation("org.copper-engine:copper-coreengine:5.4.1")
    implementation("org.copper-engine:copper-jmx-interface:5.4.1")
    implementation("org.copper-engine:copper-ext:5.4.1") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }

        // needed for needed for GitAPIException
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.3.0.202209071007-r")

    implementation("io.github.crac:org-crac:0.1.3")
}