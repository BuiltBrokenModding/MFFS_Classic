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
    }
}
rootProject.name = "mffs"