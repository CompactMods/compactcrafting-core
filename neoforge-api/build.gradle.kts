import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java-library")
    id("eclipse")
    id("idea")
    id("maven-publish")
    id("net.neoforged.gradle.userdev") version("7.0.57")
}


var envVersion: String = System.getenv("CC_VERSION") ?: "9.9.9"
if(envVersion.startsWith("v"))
    envVersion = envVersion.trimStart('v');

val isRelease: Boolean = (System.getenv("CC_RELEASE") ?: "false").equals("true", true)

var neoforge_version: String by extra

base {
    archivesName.set("compactcrafting-api")
    group = "dev.compactmods.compactcrafting"
    version = envVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

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

dependencies {
    implementation("net.neoforged:neoforge:${neoforge_version}")
}

tasks.withType<Jar> {
    manifest {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(mapOf(
                "Specification-Title" to "Compact Crafting API",
                "Specification-Vendor" to "",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to "Compact Crafting API",
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to "",
                "Implementation-Timestamp" to now
        ))
    }
}

tasks.jar {
    archiveClassifier.set("api")
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
        artifactId = "core-api"
        from(components.getByName("java"))

        artifacts {
            artifact(tasks.jar.get())
            artifact(tasks.named("sourcesJar").get())
        }
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