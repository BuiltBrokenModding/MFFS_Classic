import me.modmuss50.mpp.ReleaseType
import java.time.LocalDateTime

plugins {
    eclipse
    `maven-publish`
    id("net.neoforged.gradle.userdev") version "7.0.+"
    id("me.modmuss50.mod-publish-plugin") version "0.3.+"
    id("wtf.gofancy.git-changelog") version "1.1.+"
}

group = "dev.su5ed.mffs"
version = changelog.getVersionFromTag()

val curseForgeId: String by project
val versionJei: String by project
val versionTOP: String by project
val versionPatchouli: String by project
val versionStreamex: String by project

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoVersion: String by project
val neoVersionRange: String by project
val loaderVersionRange: String by project
val modId: String by project

val CI: Provider<String> = providers.environmentVariable("CI")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

println("Configured version: $version, Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")
minecraft {
    accessTransformers.file("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")

            modSource(project.sourceSets.main.get())
            
            dependencies { 
                runtime("one.util:streamex:$versionStreamex")
            }
        }

        create("client") {
            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            systemProperty("forge.enabledGameTestNamespaces", modId)
            programArgument("--nogui")
        }

        create("data") {
            programArguments.addAll("--mod", modId, "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
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
    implementation(group = "net.neoforged", name = "neoforge", version = neoVersion)

    // TODO verify jarjar works
    implementation(jarJar(group = "one.util", name = "streamex", version = versionStreamex)) { // Streams galore!
        jarJar.ranged(this, "[0.8.1, 0.9)")
    }

    compileOnly("mezz.jei:jei-1.20.1-common-api:$versionJei")
    compileOnly("mezz.jei:jei-1.20.1-forge-api:$versionJei")
    compileOnly(group = "mcjty.theoneprobe", name = "theoneprobe", version = versionTOP) { isTransitive = false }
}

tasks {
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
        minecraftVersions.add(minecraftVersion)
        displayName.set("MFFS $minecraftVersion-${project.version}")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
