rootProject.name = "smithy-codegen-demo"

// Include subpackages
include(":poet")
include(":codegen-node-serde-plugin")
include(":codegen-syntax-plugin")
include(":codegen-data-plugin")
include(":codegen")
include(":codegen-test")
include(":util")
include(":syntax-java")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
