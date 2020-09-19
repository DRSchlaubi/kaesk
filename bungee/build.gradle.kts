plugins {
    java
    kotlin("jvm")
    `maven-publish`
    id("com.jfrog.bintray")
}

group = "me.schlaubi.kaesk"
version = "2.1"

repositories {
    mavenCentral()
    maven("https://repo.md-5.net/service/rest/repository/browse/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    api(project(":"))
    compileOnly("net.md-5", "bungeecord-api", "1.16-R0.3")
}

val javaComponent: SoftwareComponent = components["java"]

tasks {
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
}

fun com.jfrog.bintray.gradle.BintrayExtension.pkg(block: com.jfrog.bintray.gradle.BintrayExtension.PackageConfig.() -> Unit) = pkg(delegateClosureOf(block))
fun com.jfrog.bintray.gradle.BintrayExtension.PackageConfig.version(block: com.jfrog.bintray.gradle.BintrayExtension.VersionConfig.() -> Unit) = version(delegateClosureOf(block))
fun com.jfrog.bintray.gradle.BintrayExtension.VersionConfig.gpg(block: com.jfrog.bintray.gradle.BintrayExtension.GpgConfig.() -> Unit) = gpg(delegateClosureOf(block))
