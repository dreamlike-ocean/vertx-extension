plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm")
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    java
}

group = "top.dreamlike"
version = "1.0.0beta"

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.22"))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/jakarta.ws.rs/jakarta.ws.rs-api
    api("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.22-1.0.11")
    api(libs.vertx.core)
    api(libs.vertx.web)
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}