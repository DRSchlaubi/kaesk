plugins {
    java
    kotlin("jvm")
}

group = "me.schlaubi.kaesk"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://repo.md-5.net/service/rest/repository/browse/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    api(project(":"))
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.3")
}
