import java.util.Properties

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
    id("signing")
}
val properties = Properties()
File("local.properties").inputStream().use { properties.load(it) }

repositories {
    mavenCentral()
}
kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()


    cocoapods {
        summary = "Simple Paging"
        homepage = "https://github.com/Kashif-E/Simple-Paging"
        version = "0.0.1-Alpha02"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "simple_paging"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

group = "io.github.kashif-e"
version = "0.0.1-Alpha02"


android {
    namespace = "io.github.kashif-e"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
val javaDocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

val repositoryId: String = System.getenv("SONATYPE_REPOSITORY_ID")
val sonatypeUsername: String = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String = System.getenv("SONATYPE_PASSWORD")
publishing {
    repositories {
        repositories {
            maven {
                name = "oss"
                val releaseRepoUrl =
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl =
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString()
                        .endsWith("SNAPSHOT")
                ) snapshotsRepoUrl else releaseRepoUrl
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
    }

    publications {
        withType<MavenPublication> {
            artifact(javaDocJar)
            pom {
                name.set("Simple Paging")
                description.set("A simple paging library for Android and iOS written in Kotlin Multiplatform")
                url.set("https://www.github.com/Kashif-E/Simple-Paging")
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://www.github.com/Kashif-E/Simple-Paging/issues")
                }
                scm {
                    connection.set("https://www.github.com/Kashif-E/Simple-Paging.git")
                    url.set("https://www.github.com/Kashif-E/Simple-Paging")
                }
                developers {
                    developer {
                        name.set("Kashif Mehmood")
                        email.set("kashismails@gmail.com")
                    }
                }

            }
        }
    }
}

signing {
    useGpgCmd()
    val signingKey: String =
        properties.getProperty("GPG_SIGNING_KEY").toString()
            ?: error("Missing env variable: GPG_KEY")
    val signingPassword: String =
        System.getenv("GPG_KEY_PASSWORD") ?: error("Missing env variable: GPG_KEY_PASSWORD")
    logger.info("signingKey: $signingKey")
    logger.info("signingPassword: $signingPassword")
    useInMemoryPgpKeys(
        signingKey,
        signingPassword
    )
    sign(publishing.publications)
}
