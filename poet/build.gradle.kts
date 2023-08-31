description = "Java poet clone"
extra["displayName"] = "Smithy :: Java Poet"
extra["moduleName"] = "mx.sugus.javapoet"

val smithyVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

buildscript {
    val smithyVersion: String by project

    repositories {
        mavenCentral()
    }
    dependencies {
        "classpath"("software.amazon.smithy:smithy-cli:$smithyVersion")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "mx.sugus.syntax.java"
            artifactId = "smithy-java-syntax-poet"
            version = "0.1"

            from(components["java"])
        }
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
