plugins {
    kotlin("jvm") version "1.3.61"
    groovy
}

group = "pl.tfij.image"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-imaging:1.0-alpha1")

    testCompile("org.codehaus.groovy:groovy-all:2.5.8")
    testCompile("org.spockframework:spock-core:1.3-groovy-2.5")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}