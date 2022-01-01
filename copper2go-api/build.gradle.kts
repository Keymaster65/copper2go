group = "io.github.keymaster65"
version = "3.0.1"

plugins {
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            pom {
                name.set("copper2go-api")
                description.set("API for copper2go workflows")
                url.set("https://github.com/Keymaster65/copper2go")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Keymaster65")
                        name.set("Wolf Sluyterman van Langeweyde")
                        email.set("wolf.sluyterman@scoop-software.de")
                    }
                }
                scm {
                    connection.set("https://github.com/Keymaster65/copper2go.git")
                    developerConnection.set("https://github.com/Keymaster65/copper2go.git")
                    url.set("https://github.com/Keymaster65/copper2go/")
                }
            }
        }
    }
}

dependencies {
    implementation("org.copper-engine:copper-coreengine:5.4.1")

    testImplementation("org.assertj:assertj-assertions-generator:2.+")
}