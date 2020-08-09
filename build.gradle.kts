import org.gradle.kotlin.dsl.codegen.generateApiExtensionsJar

plugins {
    java
    application
    distribution
    `maven-publish`
    jacoco
    id("com.github.jk1.dependency-license-report") version "1.14"
}

group = "de.wolfsvl"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
    }
}

task<JacocoMerge>("jacocoMerge") {
    destinationFile = File("$buildDir/jacoco/allTestCoverage.exec")
    executionData = fileTree("$buildDir/jacoco")
}

tasks.withType<JacocoReport> {
    reports {
        executionData.setFrom("$buildDir/jacoco/allTestCoverage.exec")
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}
dependencies {
    implementation("org.copper-engine:copper-ext:5.2.0")

    // needed for exceptions
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.7.0.202003110725-r")

    implementation("org.copper-engine:copper-coreengine:5.2.0")
    implementation("org.copper-engine:copper-jmx-interface:5.2.0")

    implementation("org.slf4j:slf4j-api:1.8.0-beta2")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

application {
    mainClassName = "de.wolfsvl.copper2go.Application"
    applicationDefaultJvmArgs = listOf("-Dlogback.configurationFile=src/main/resources/logback.xml")
}