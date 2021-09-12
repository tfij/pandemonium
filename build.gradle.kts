import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.Platform

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.6.0")
    }
}

plugins {
    kotlin("jvm") version "1.5.30"
    application
    groovy
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.3"
}
apply(plugin = "io.github.fvarrui.javapackager.plugin")

group = "pl.tfij.image"
// version = scmVersion.version
version = "1.0.1SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    modules("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-imaging:1.0-alpha2")
    implementation("com.google.inject:guice:5.0.1")

    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
}

application {
    mainClass.set("pl.tfij.image.pandemonium.gui.Main")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<PackageTask>("packageForLinux") {
    group = "package"
    dependsOn(tasks["build"])
    mainClass = "pl.tfij.image.pandemonium.gui.Main"
    isBundleJre = true
    isAdministratorRequired = false
    isGenerateInstaller = true
    platform = Platform.linux
    outputDirectory = File(project.buildDir, "binaries")
}

tasks.register<PackageTask>("packageForWindows") {
    group = "package"
    dependsOn(tasks["build"])
    mainClass = "pl.tfij.image.pandemonium.gui.Main"
    isCreateZipball = false
    additionalModulePaths = listOf(file("src"))
    isBundleJre = true
    isAdministratorRequired = false
    isGenerateInstaller = true
    platform = Platform.windows
    outputDirectory = File(project.buildDir, "binaries")
}

tasks.register<PackageTask>("packageForMac") {
    group = "package"
    dependsOn(tasks["build"])
    mainClass = "pl.tfij.image.pandemonium.gui.Main"
    isBundleJre = true
    isAdministratorRequired = false
    isGenerateInstaller = true
    isCreateTarball = true
    platform = Platform.mac
    outputDirectory = File(project.buildDir, "binaries")
}

tasks.register("packageForAllPlatforms") {
    group = "package"
    dependsOn(tasks["packageForLinux"], tasks["packageForWindows"], tasks["packageForMac"])
}
