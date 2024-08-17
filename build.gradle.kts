plugins {
    id("org.unbroken-dome.test-sets") version "4.1.0"
    id("info.solidsoft.pitest") version "1.15.0"
}

apply(plugin = "info.solidsoft.pitest.aggregator")

group = "io.github.keymaster65"

allprojects {
    apply(plugin = "org.unbroken-dome.test-sets")
    apply(plugin = "info.solidsoft.pitest")
}