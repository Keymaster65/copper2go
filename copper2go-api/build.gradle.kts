import java.net.URI

pitest {
    targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.api.*"))
}

group = "io.github.keymaster65"
version = "3.4.0-SNAPSHOT"

plugins {
    `maven-publish`
    `java-library`
    signing
}

apply(plugin = "signing")

java {
    withSourcesJar()
    withJavadocJar()
}

artifacts {
    archives(tasks.getByName<Jar>("sourcesJar"))
    archives(tasks.getByName<Jar>("javadocJar"))
}

signing {
    sign(configurations.archives.name)
}

tasks.withType<Test> {
    jvmArgs = listOf("-Dorg.copperengine.workflow.compiler.options=-target,21,-source,21")
}

publishing {
    publications {
        signing.sign(
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
        )
    }
}



publishing {
    repositories {
        maven {
            credentials {
                username = "keymaster65"
                password = "W0lf-S0n1r12"
            }
            if (version.toString().endsWith("-SNAPSHOT")) {
                url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
            } else {
                url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
        }
    }
}


dependencies {
    api("org.copper-engine:copper-coreengine:5.5.0")
    api("org.slf4j:slf4j-api:2.0.13")
    api("com.fasterxml.jackson.core:jackson-databind:2.17.1")

    testImplementation("org.assertj:assertj-assertions-generator:2.2.1")
}