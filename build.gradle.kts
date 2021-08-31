plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    application
    groovy
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "pl.tfij.image"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    modules("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-imaging:1.0-alpha1")
    implementation("com.google.inject:guice:5.0.1")

    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
}

application {
    mainClassName = "pl.tfij.image.pandemonium.gui.Main"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
