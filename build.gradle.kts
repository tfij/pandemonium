plugins {
    kotlin("jvm") version "1.5.30"
    application
    groovy
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.3"
}

group = "pl.tfij.image"
version = scmVersion.version

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
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.0")
    implementation("ch.qos.logback:logback-classic:1.2.7")

    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
}

application {
    mainClass.set("pl.tfij.image.pandemonium.gui.Main")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
