rootProject.name = "smithy-codegen-demo"

// Include subpackages
include(":poet")
include(":codegen-plugin")
include(":codegen-syntax-plugin")
include(":codegen-data-plugin")
include(":codegen")
include(":codegen-test")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
