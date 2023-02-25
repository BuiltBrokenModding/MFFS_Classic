import net.minecraftforge.gradle.userdev.tasks.JarJar
import java.time.LocalDateTime

plugins {
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version "5.1.+"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("io.github.CDAGaming.cursegradle") version "1.6.+"
}

group = "dev.su5ed.mffs"
version = "5.0.0-alpha.2"

val versionMc: String by project
val curseForgeId: String by project
val publishReleaseType = System.getenv("PUBLISH_RELEASE_TYPE") ?: "beta"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

minecraft {
    mappings("parchment", "2022.10.16-1.19.2")

    runs {
        create("client")
        create("server")
        create("gameTestServer")

        create("data") {
            args("--mod", "mffs", "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }

        all {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            forceExit = false

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
                "Specification-Version" to project.version,
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

curseforge {
    apiKey = System.getenv("CURSEFORGE_TOKEN") ?: "UNKNOWN"
    project {
        id = curseForgeId
        releaseType = publishReleaseType
        mainArtifact(tasks.jar.get()) {
            displayName = "MFFS $versionMc-${project.version}"
        }
        addGameVersion("Forge")
        addGameVersion(versionMc)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
