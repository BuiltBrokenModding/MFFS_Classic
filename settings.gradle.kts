pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net")
        }
        maven {
            name = "Parchment"
            url = uri("https://maven.parchmentmc.org")
        }
        maven {
            name = "Gofancy"
            url = uri("https://maven.gofancy.wtf/releases")
        }
    }
}
rootProject.name = "mffs"