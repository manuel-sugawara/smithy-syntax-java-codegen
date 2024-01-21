description = "Java poet clone"
extra["displayName"] = "Smithy :: Syntax Java"
extra["moduleName"] = "mx.sugus.syntax.java"

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
            artifactId = "smithy-java-syntax-util"
            version = "0.1"

            from(components["java"])
        }
    }
}

dependencies {
    implementation(project(":util"))
    implementation(project(":poet"))
    implementation(project(":codegen"))
    implementation("software.amazon.smithy:smithy-codegen-core:$smithyVersion")
    implementation("software.amazon.smithy:smithy-model:$smithyVersion")
    testImplementation("org.mockito:mockito-core:3.+")
}
