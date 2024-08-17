rootProject.name = "copper2go"

// removing copper2go-api does not lead ConcurrentModificationException
// it is needed for connector-api
include(":copper2go-api")

// removing connector-api does not lead ConcurrentModificationException
include(":connector-api")
