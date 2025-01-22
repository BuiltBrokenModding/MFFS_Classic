import me.modmuss50.mpp.ReleaseType
import net.minecraftforge.gradle.userdev.tasks.JarJar
import java.time.LocalDateTime

plugins {
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
    id("wtf.gofancy.git-changelog") version "1.1.+"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    // Required to run mixin mods in dev
    id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
}

group = "dev.su5ed.mffs"
version = changelog.getVersionFromTag()

val versionMc: String by project
val versionForge: String by project
val curseForgeId: String by project
val modrinthId: String by project
val versionJei: String by project
val versionTOP: String by project
val versionPatchouli: String by project
val publishReleaseType = System.getenv("PUBLISH_RELEASE_TYPE") ?: "beta"
val changelogText = changelog.generateChangelog(1, true)

val CI: Provider<String> = providers.environmentVariable("CI")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

logger.lifecycle("\nConfigured version: $version")

minecraft {
    mappings("parchment", "2023.06.26-1.20.1")

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

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
        name = "Jared"
        url = uri("https://maven.blamejared.com/")
    }
    maven {
        name = "Modmaven"
        url = uri("https://modmaven.dev/")
    }
    maven {
        name = "TOP"
        url = uri("https://maven.k-4u.nl")
    }
    mavenCentral()
}

dependencies {
    minecraft(group = "net.minecraftforge", name = "forge", version = "$versionMc-$versionForge")

    minecraftLibrary(jarJar(group = "one.util", name = "streamex", version = "0.8.1")) { // Streams galore!
        jarJar.ranged(this, "[0.8.1, 0.9)")
    }

    compileOnly(fg.deobf("mezz.jei:jei-$versionMc-common-api:$versionJei"))
    compileOnly(fg.deobf("mezz.jei:jei-$versionMc-forge-api:$versionJei"))
    compileOnly(fg.deobf(create(group = "mcjty.theoneprobe", name = "theoneprobe", version = versionTOP).apply { isTransitive = false }))

    runtimeOnly(fg.deobf("mezz.jei:jei-$versionMc-forge:$versionJei"))
    runtimeOnly(fg.deobf("vazkii.patchouli:Patchouli:$versionPatchouli"))
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

publishMods {
    file.set(tasks.jarJar.flatMap { it.archiveFile })
    changelog.set(provider { project.changelog.generateChangelog(1, true) })
    type.set(providers.environmentVariable("PUBLISH_RELEASE_TYPE").map(ReleaseType::of).orElse(ReleaseType.STABLE))
    modLoaders.add("forge")
    dryRun.set(!CI.isPresent)

    curseforge {
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        projectId.set(curseForgeId)
        minecraftVersions.add(versionMc)
        displayName.set("MFFS $versionMc-${project.version}")
    }
    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set(modrinthId)
        minecraftVersions.add(versionMc)
        displayName.set("MFFS $versionMc-${project.version}")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
