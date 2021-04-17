group = "io.github.keymaster65"
version = "1.2"

plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation("org.copper-engine:copper-coreengine:5+")

    testImplementation("org.assertj:assertj-assertions-generator:2+")
}