pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Gofancy"
            url = uri("https://maven.gofancy.wtf/releases")
        }
        mavenLocal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "mffs"