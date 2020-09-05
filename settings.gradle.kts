rootProject.name = "kaesk"
include("example")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
include(":kaesk-bukkit")
project(":kaesk-bukkit").projectDir = file("bukkit")
include(":kaesk-bungee")
project(":kaesk-bungee").projectDir = file("bungee")
include(":kaesk-nukkit")
project(":kaesk-nukkit").projectDir = file("nukkit")
