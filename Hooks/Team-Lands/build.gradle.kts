dependencies {
    // Latest public API artifact. Runtime acceptance is performed against the exact
    // Lands 8.2.3 production plugin; its API is not published as a Maven artifact.
    compileOnly("com.incredibleplugins:lands-api:8.0.0")
    testImplementation(project(":API"))
    testImplementation("com.incredibleplugins:lands-api:8.0.0")
}
