import net.minecraftforge.gradle.userdev.UserDevExtension
import java.text.SimpleDateFormat
import java.util.*

var modVersion: String = System.getenv("CC_VERSION") ?: "9.9.9"
if(modVersion.startsWith("v"))
    modVersion = modVersion.trimStart('v');

var mod_id: String by extra

plugins {
    id("idea")
    id("eclipse")
    id("maven-publish")
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
}

base {
    group = "dev.compactmods.compactcrafting"
    version = modVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

var minecraft_version: String by extra
var forge_version: String by extra
var parchment_version: String by extra

sourceSets.named("main") {
    resources {
        //The API has no resources
        setSrcDirs(emptyList<String>())
    }
}

sourceSets.named("test") {
    resources {
        //The test module has no resources
        setSrcDirs(emptyList<String>())
    }
}

configure<UserDevExtension> {
    mappings("parchment", "${parchment_version}-${minecraft_version}")
}

dependencies {
    minecraft("net.minecraftforge", "forge", version = "${minecraft_version}-${forge_version}")
}

tasks.withType<Jar> {

    manifest {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(mapOf(
                "Specification-Title" to "Compact Crafting API",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to "Compact Crafting API",
                "Implementation-Version" to archiveVersion,
                "Implementation-Timestamp" to now,
                "FMLModType" to "GAMELIBRARY"
        ))
    }
}

tasks.jar {
    archiveClassifier.set("api")
    finalizedBy("reobfJar")
}

tasks.named<Jar>("sourcesJar") {
    archiveClassifier.set("api-sources")
}

artifacts {
    archives(tasks.jar.get())
    archives(tasks.named("sourcesJar").get())
}

val PACKAGES_URL = System.getenv("GH_PKG_URL") ?: "https://maven.pkg.github.com/compactmods/compactcrafting-core"
publishing {
    publications.register<MavenPublication>("api") {
        from(components.getByName("java"))
    }

    repositories {
        // GitHub Packages
        maven(PACKAGES_URL) {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}