plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm")
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    java
    id("com.google.devtools.ksp") version "1.8.22-1.0.11"
}

group = "top.dreamlike"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()

}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}
application {
    // Define the main class for the application.
    mainClass.set("top.dreamlike.AppKt")
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation(project(":vertx-ksp"))
    ksp(project(":vertx-ksp"))
    implementation(project(":vertx-dsl"))
}

tasks.test {
    useJUnitPlatform()
}