plugins {
    id("org.unbroken-dome.test-sets") version "4.1.0"
    id("info.solidsoft.pitest") version "1.15.0"
}

allprojects {
    // removing test-sets does not lead ConcurrentModificationException
    apply(plugin = "org.unbroken-dome.test-sets")

    // removing test-sets does not lead ConcurrentModificationException
    // but to "Unresolved reference: implementation", which seems to be success
    apply(plugin = "info.solidsoft.pitest")
}