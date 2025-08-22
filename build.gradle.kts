import me.modmuss50.mpp.ReleaseType

plugins {
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.107"
    id("me.modmuss50.mod-publish-plugin") version "0.8.+"
    id("wtf.gofancy.git-changelog") version "1.1.+"
    id("org.moddedmc.wiki.toolkit") version "0.3.2"
}

group = "dev.su5ed.mffs"
version = changelog.getVersionFromTag()

val curseForgeId: String by project
val modrinthId: String by project
val versionJei: String by project
val versionTOP: String by project
val versionPatchouli: String by project
val versionStreamex: String by project

val versionMc: String by project
val neoVersion: String by project
val parchmentVersion: String by project
val modId: String by project

val CI: Provider<String> = providers.environmentVariable("CI")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

println("Configured version: $version, Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")
neoForge {
    version = neoVersion
    accessTransformers {
        from(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
    }
    parchment {
        mappingsVersion = parchmentVersion
        minecraftVersion = versionMc
    }
    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            systemProperty("wiki_exporter.config.path", file("src/main/resources/exporter-config.json").absolutePath)
            additionalRuntimeClasspathConfiguration.dependencies.add(dependencies.create(group = "one.util", name = "streamex", version = versionStreamex))
        }

        create("client") {
            client()
            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            programArgument("--nogui")
        }

        create("data") {
            clientData()
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        create("exportClient") {
            client()
            systemProperty("wiki_exporter.enabled", "true")
        }
        create("exportServer") {
            server()
            programArgument("nogui")
            systemProperty("wiki_exporter.enabled", "true")
        }
    }
    mods { 
        create("mffs") {
            sourceSet(sourceSets.main.get())
        }
    }
}

wiki {
    docs {
        register("mffs") {             
            root = file("docs")
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
    implementation(jarJar(group = "one.util", name = "streamex", version = versionStreamex))

    // compile against the JEI API but do not include it at runtime     
    compileOnly("mezz.jei:jei-1.21.8-common-api:$versionJei")
    compileOnly("mezz.jei:jei-1.21.8-neoforge-api:$versionJei")
    // at runtime, use the full JEI jar for NeoForge
    runtimeOnly("mezz.jei:jei-1.21.8-neoforge:$versionJei")

    compileOnly(group = "mcjty.theoneprobe", name = "theoneprobe", version = versionTOP) { isTransitive = false }
    runtimeOnly("org.sinytra:wiki-exporter-neoforge:2.1.0+1.21.8")
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
                "Implementation-Vendor" to "Su5eD"
            )
        }
    }
}

publishMods {
    file.set(tasks.jar.flatMap { it.archiveFile })
    changelog.set(provider { project.changelog.generateChangelog(1, true) })
    type.set(providers.environmentVariable("PUBLISH_RELEASE_TYPE").map(ReleaseType::of).orElse(ReleaseType.STABLE))
    modLoaders.add("neoforge")
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
