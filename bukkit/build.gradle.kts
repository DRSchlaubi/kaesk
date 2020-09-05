plugins {
    java
    kotlin("jvm")
}

group = "me.schlaubi.kaesk"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":"))
    compileOnly("org.spigotmc", "spigot-api", "1.16.1-R0.1-SNAPSHOT")
    compileOnly(kotlin("stdlib-jdk8"))
}
