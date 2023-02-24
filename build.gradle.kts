import net.minecraftforge.gradle.userdev.tasks.JarJar
import java.time.LocalDateTime

plugins {
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version "5.1.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("me.qoomon.git-versioning") version "6.3.+"
}

version = "0.0.0-SNAPSHOT"
group = "dev.su5ed.mffs"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

gitVersioning.apply {
    rev {
        version = "\${describe.tag.version.major}.\${describe.tag.version.minor}.\${describe.tag.version.patch.plus.describe.distance}"
    }
}

minecraft {
    mappings("parchment", "2022.10.16-1.19.2")

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "mffs")
            forceExit = false

            mods {
                create("mffs") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "mffs")
            forceExit = false

            mods {
                create("mffs") {
                    source(sourceSets.main.get())
                }
            }
        }


        create("gameTestServer") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "mffs")
            forceExit = false

            mods {
                create("mffs") {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            forceExit = false

            args("--mod", "mffs", "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))

            mods {
                create("mffs") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
    }
}

repositories {
    maven {
        name = "Modmaven"
        url = uri("https://modmaven.dev/")
    }
    mavenCentral()
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "1.19.2-43.1.47")

    minecraftLibrary(jarJar(group = "one.util", name = "streamex", version = "0.8.1")) { // Streams galore!
        jarJar.ranged(this, "[0.8.1, 0.9)")
    }

    runtimeOnly(fg.deobf("mekanism:Mekanism:1.19.2-10.3.5.474"))
    runtimeOnly(fg.deobf("mekanism:Mekanism:1.19.2-10.3.5.474:generators"))
}

reobf {
    create("jarJar")
}

tasks {
    jar {
        finalizedBy("reobfJar")
    }
    
    named<JarJar>("jarJar") {
        finalizedBy("reobfJarJar")
    }
    
    withType<Jar> {
        manifest {
            attributes(
                "Specification-Title" to project.name,
                "Specification-Vendor" to "Built Broken Modding",
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "Su5eD",
                "Implementation-Timestamp" to LocalDateTime.now()
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
