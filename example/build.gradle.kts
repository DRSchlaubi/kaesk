plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"

}

group = "me.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

dependencies {
    implementation(project(":kaesk-bukkit"))
    implementation(project(":kaesk-bungee"))

    compileOnly("org.spigotmc", "spigot-api", "1.16.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.3")
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_14
}