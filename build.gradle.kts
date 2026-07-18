import me.modmuss50.mpp.ReleaseType

plugins {
    `maven-publish`
    id("net.neoforged.moddev") version "2.0.141"
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
    id("wtf.gofancy.git-changelog") version "1.1.+"
    id("org.moddedmc.wiki.toolkit") version "0.4.1"
}

group = "dev.su5ed.mffs"
version = changelog.getVersionFromTag()

val curseForgeId = project.property("curseForgeId") as String
val modrinthId = project.property("modrinthId") as String
val versionJei = project.property("versionJei") as String
val versionPatchouli = project.property("versionPatchouli") as String
val versionStreamex = project.property("versionStreamex") as String

val versionMc = project.property("versionMc") as String
val neoVersion = project.property("neoVersion") as String
val modId = project.property("modId") as String

val CI: Provider<String> = providers.environmentVariable("CI")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(25))

println("Configured version: $version, Java: ${System.getProperty("java.version")}, JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}), Arch: ${System.getProperty("os.arch")}")
neoForge {
    version = neoVersion

    accessTransformers.from("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        create("client") {
            client()
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
            root = file("docs/mffs")
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
    mavenCentral()
}

dependencies {
    implementation(jarJar(group = "one.util", name = "streamex", version = versionStreamex))

    // compile against the JEI API but do not include it at runtime     
    compileOnly("mezz.jei:jei-26.1.2-common-api:$versionJei")
    compileOnly("mezz.jei:jei-26.1.2-neoforge-api:$versionJei")
    // at runtime, use the full JEI jar for NeoForge
    runtimeOnly("mezz.jei:jei-26.1.2-neoforge:$versionJei")

//    runtimeOnly("org.sinytra:item-asset-export-neoforge:1.0.2+1.21")
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
        client.set(true)
        server.set(true)
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
