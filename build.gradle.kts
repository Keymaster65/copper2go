plugins {
    id("org.unbroken-dome.test-sets") version "4.1.0"
    id("info.solidsoft.pitest") version "1.15.0"
}

allprojects {
    // removing test-sets does not lead ConcurrentModificationException
    // but to "Unresolved reference: implementation"
    apply(plugin = "org.unbroken-dome.test-sets")

    // removing info.solidsoft.pitest does not lead ConcurrentModificationException
    apply(plugin = "info.solidsoft.pitest")

    pitest {
        addJUnitPlatformLauncher = false
    }
}