@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import java.net.URI

plugins {
    id("fabric-loom") version "1.5-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    kotlin("plugin.serialization") version "2.0.0-RC3"
}

val props = Properties(project.properties)

version = props.modVersion
group = props.mavenGroup

base {
    archivesName = props.archivesBaseName
}

loom {
    splitEnvironmentSourceSets()

    accessWidenerPath = file("src/main/resources/amethyst.accesswidener")

    mods {

        maybeCreate("amethyst").apply {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${props.minecraftVersion}")
    modImplementation("net.fabricmc:fabric-loader:${props.loaderVersion}")

    mappings("net.fabricmc:yarn:${props.yarnMappings}:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${props.fabricVersion}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${props.kotlinVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0-RC")
}

tasks {
    processResources {
        inputs.property("version", project.version)
        inputs.property("minecraft_version", props.minecraftVersion)
        inputs.property("loader_version", props.loaderVersion)
        inputs.property("fabric_version", props.fabricVersion)

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraft_version" to props.minecraftVersion,
                "loader_version" to props.loaderVersion,
                "fabric_version" to props.fabricVersion
            )
        }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${props.archivesBaseName}" }
        }
    }

    withType(JavaCompile::class).configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}


java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

//publishing {
//    publications {
//        mavenJava(MavenPublication) {
//            from components.java
//        }
//    }
//
//    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
//    repositories {
//        // Add repositories to publish to here.
//        // Notice: This block does NOT have the same function as the block in the top level.
//        // The repositories here will be used for publishing your artifact, not for
//        // retrieving dependencies.
//    }
//}

class Properties(properties: Map<String, *>) {
    val minecraftVersion = properties["minecraft_version"]!! as String
    val yarnMappings = properties["yarn_mappings"]!! as String
    val loaderVersion = properties["loader_version"]!! as String
    val modVersion = properties["mod_version"]!! as String
    val mavenGroup = properties["maven_group"]!! as String
    val archivesBaseName = properties["archives_base_name"]!! as String
    val fabricVersion = properties["fabric_version"]!! as String
    val kotlinVersion = properties["fabric_kotlin_version"]!! as String
}
