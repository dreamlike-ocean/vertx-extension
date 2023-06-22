plugins {
    id("java")
}

group = "top.dreamlike"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/io.vertx/vertx-core
    implementation("io.vertx:vertx-core:4.4.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("libBuild") {
    println("libBuild")
}