description = "Generates code from Smithy models"
extra["displayName"] = "Smithy :: Codegen :: Java "
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
    testImplementation("org.mockito:mockito-core:3.+")
}
