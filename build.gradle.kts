plugins {
    kotlin("jvm") version "1.3.61"
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
    implementation("com.google.inject:guice:4.0")

    testCompile("org.codehaus.groovy:groovy-all:2.5.8")
    testCompile("org.spockframework:spock-core:1.3-groovy-2.5")
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
