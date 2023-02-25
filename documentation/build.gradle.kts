

//tasks.runGenerator(type: JavaExec) {
//    classpath = sourceSets.main.runtimeClasspath
//    main = 'ServiceDocumentation'
//    args = ['--file','build/generated/serviceData.xml']
//}

tasks.register<JavaExec>("runGenerator") {
  classpath = sourceSets["main"].runtimeClasspath;
  mainClass.set("Hello")
}