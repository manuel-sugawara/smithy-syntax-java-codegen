rootProject.name = "smithy-codegen-demo"

// Include subpackages
include(":poet")
include(":codegen-plugin")
include(":codegen")
include(":codegen-test")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
