description = "Generates code from Smithy models"
extra["displayName"] = "Smithy :: Codegen Smithy Smithy Node serde Plugin"
extra["moduleName"] = "mx.sugus.codegen.plugin.node"

val smithyVersion: String by project

buildscript {
    val smithyVersion: String by project

    repositories {
        mavenCentral()
    }
    dependencies {
        "classpath"("software.amazon.smithy:smithy-cli:$smithyVersion")
    }
}

dependencies {
    implementation(project(":codegen"))
    implementation(project(":codegen-data-plugin"))
    implementation(project(":poet"))
    implementation("software.amazon.smithy:smithy-codegen-core:$smithyVersion")
    implementation("software.amazon.smithy:smithy-model:$smithyVersion")
    implementation("software.amazon.smithy:smithy-rules-engine:$smithyVersion")
    implementation("software.amazon.smithy:smithy-waiters:$smithyVersion")
    implementation("software.amazon.smithy:smithy-protocol-test-traits:$smithyVersion")
    implementation("mx.sugus.syntax.java:smithy-java-syntax-traits:0.1")
    testImplementation("org.mockito:mockito-core:3.+")
}
