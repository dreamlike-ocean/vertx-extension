/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/8.1.1/userguide/multi_project_builds.html
 */
val vertx_version = "4.4.2"
val kotlin_version = "1.8.22"
val ksp_version by extra ("1.8.22-1.0.11")
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm") version "1.8.22"
    }
}
//
//plugins {
//    // Apply the foojay-resolver plugin to allow automatic download of JDKs
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
//}
//


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("vertx-core","io.vertx:vertx-core:$vertx_version")
            library("vertx-web", "io.vertx:vertx-web:$vertx_version")
            library("vertx-coroutine", "io.vertx:vertx-lang-kotlin-coroutines:$vertx_version")
        }
    }
}


rootProject.name = "vertx-extension"
include("vertx-dsl")
include("vertx-ksp")
include("example")
