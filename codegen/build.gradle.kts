description = "Generates code from Smithy models"
extra["displayName"] = "Smithy :: Codegen"
extra["moduleName"] = "mx.sugus.codegen"

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
    implementation("software.amazon.smithy:smithy-codegen-core:$smithyVersion")
    implementation("software.amazon.smithy:smithy-model:$smithyVersion")
    implementation("software.amazon.smithy:smithy-rules-engine:$smithyVersion")
    implementation("software.amazon.smithy:smithy-waiters:$smithyVersion")
    implementation("software.amazon.smithy:smithy-protocol-test-traits:$smithyVersion")
    testImplementation("org.mockito:mockito-core:3.+")
}
