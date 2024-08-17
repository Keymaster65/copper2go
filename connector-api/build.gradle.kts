pitest {
  targetClasses.set(setOf<String>("io.github.keymaster65.copper2go.api.connector.*"))
}

dependencies {
    // removing copper2go-api dependency does not lead ConcurrentModificationException
 implementation(project(":copper2go-api"))
}