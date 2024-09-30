plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.pi4j:pi4j-core:2.6.0")
    implementation("com.pi4j:pi4j-plugin-raspberrypi:2.6.0")
    implementation("com.pi4j:pi4j-plugin-pigpio:2.6.0")
    implementation("com.pi4j:pi4j-plugin-mock:2.6.0")
    implementation("com.pi4j:pi4j-plugin-linuxfs:2.6.0")
    implementation("com.pi4j:pi4j-plugin-gpiod:2.6.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}