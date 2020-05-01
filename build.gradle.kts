import com.jfrog.bintray.gradle.BintrayExtension

plugins {
    java
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

group = "me.schlaubi"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("org.jetbrains", "annotations", "19.0.0")
    compileOnly("org.spigotmc", "spigot-api", "1.15.2-R0.1-SNAPSHOT")

    // Tests
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.spigotmc", "spigot-api", "1.15.2-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")

}

val javaComponent = components["java"]

tasks {
    test {
        useJUnitPlatform()
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // Seems like it is needed
        jvmArgs!!.add("--enable-preview")
    }


    val sourcesJar = task<Jar>("sourcesJar") {
        dependsOn(classes)
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val buildJavadoc = task<Exec>("buildJavadoc") {
        executable = System.getProperty("java.home") + "/bin/javadoc"
        args("--enable-preview", "-source", "14", "-sourcepath", "src/main/java", "-d", "./docs", "-subpackages", "me.schlaubi.kaesk", "--class-path", compileJava.get().classpath.asPath, "-tag", "implNote:a:Implementation Note: ")
    }

    val javadocJar = task<Jar>("javadocJar") {
        dependsOn(buildJavadoc)
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        archiveClassifier.set("javadoc")
        from(project.files("docs"))
    }

    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
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
    sourceCompatibility = JavaVersion.VERSION_14
}

fun BintrayExtension.pkg(block: BintrayExtension.PackageConfig.() -> Unit) = pkg(delegateClosureOf(block))
fun BintrayExtension.PackageConfig.version(block: BintrayExtension.VersionConfig.() -> Unit) = version(delegateClosureOf(block))
fun BintrayExtension.VersionConfig.gpg(block: BintrayExtension.GpgConfig.() -> Unit) = gpg(delegateClosureOf(block))
