import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jmailen.kotlinter") version "3.8.0"
}

val pluginVersion: String by project.ext
val apiVersion: String by project.ext

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://libraries.minecraft.net")
    maven(url = "https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.github.com/tororo1066/TororoPluginAPI")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:$pluginVersion-R0.1-SNAPSHOT")
    compileOnly("tororo1066:commandapi:$apiVersion")
    compileOnly("tororo1066:base:$apiVersion")
    implementation("tororo1066:tororopluginapi:$apiVersion")
    compileOnly("com.mojang:brigadier:1.0.18")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}

tasks.named("build") {
    dependsOn("shadowJar")
}