val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.20-RC"
}

group = "pt.ipc.estgoh.francisco_luis"
version = "0.0.1"
application {
    mainClass.set("pt.ipc.estgoh.francisco_luis.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.6.0")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("org.mindrot:jbcrypt:0.4")


    //Exposed ORM library
    val exposedVersion: String by project
    dependencies {
        implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
        implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
        implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    }

    implementation("com.zaxxer:HikariCP:4.0.3") // JDBC Connection Pool
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}