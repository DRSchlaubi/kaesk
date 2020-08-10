import com.jfrog.bintray.gradle.BintrayExtension
import java.net.URL

plugins {
    java
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

group = "me.schlaubi"
version = "1.2"

repositories {
    jcenter()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("org.jetbrains", "annotations", "19.0.0")
    compileOnly("org.spigotmc", "spigot-api", "1.15.2-R0.1-SNAPSHOT")

    compileOnly(kotlin("stdlib-jdk8"))

    // Tests
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.spigotmc", "spigot-api", "1.16.1-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
    testImplementation("org.mockito:mockito-core:2.+")
}

val javaComponent: SoftwareComponent = components["java"]

tasks {
    test {
        useJUnitPlatform()
    }


    val sourcesJar = task<Jar>("sourcesJar") {
        dependsOn(classes)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }


    val javadocJar = task<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        archiveClassifier.set("javadoc")
        from(dokkaHtml)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(javaComponent)
                artifact(sourcesJar)
                artifact(javadocJar)
            }
        }
    }

    compileTestJava {
        targetCompatibility = "11"
        sourceCompatibility = "11"
    }

    dokkaHtml {
        outputDirectory = "docs/"

        dokkaSourceSets {
            configureEach {
                includeNonPublic = false

                sourceLink {
                    path = "src"

                    url = "https://github.com/DRSchlaubi/kaesk/tree/master/src"

                    lineSuffix = "#L"
                }

                jdkVersion = 8

                // seems to bee bugged in this version
//                externalDocumentationLink {
//                    url = URL("https://hub.spigotmc.org/javadocs/spigot/")
//                }
            }
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications("mavenJava")
    pkg {
        repo = "maven"
        name = "kaesk"
        setLicenses("GPL-3.0")
        vcsUrl = "https://github.com/DRSchlaubi/kaesk.git"
        version {
            name = project.version as String
            gpg {
                sign = true
                passphrase = System.getenv("GPG_PASS")
            }
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

fun BintrayExtension.pkg(block: BintrayExtension.PackageConfig.() -> Unit) = pkg(delegateClosureOf(block))
fun BintrayExtension.PackageConfig.version(block: BintrayExtension.VersionConfig.() -> Unit) = version(delegateClosureOf(block))
fun BintrayExtension.VersionConfig.gpg(block: BintrayExtension.GpgConfig.() -> Unit) = gpg(delegateClosureOf(block))
