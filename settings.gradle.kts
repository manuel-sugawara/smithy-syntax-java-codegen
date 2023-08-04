rootProject.name = "smithy-codegen-demo"

// Include subpackages
include(":codegen-jv")
include(":codegen")
include(":codegen-test")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

