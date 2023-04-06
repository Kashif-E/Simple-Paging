plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("maven-publish")
    id("signing")
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

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    cocoapods {
        summary = "Simple Paging"
        homepage = "https://github.com/Kashif-E/Simple-Paging"
        version = "0.0.1-Alpha01"
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

val javaDocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

val repositoryId: String = System.getenv("SONATYPE_REPOSITORY_ID")
val sonatypeUsername: String = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String = System.getenv("SONATYPE_PASSWORD")
publishing {
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

    publications {
        withType<MavenPublication> {
            pom {
                name.set("Simple Paging")
                description.set("Simple Paging library for Android and iOS using Kotlin Multiplatform")
                licenses {
                    license { name.set("MIT") }
                    url.set("https://opensource.org/licenses/MIT")
                }
                url.set("https://www.github.com/Kashif-E/Simple-Paging.git")
                issueManagement {
                    system.set("GitHub")
                    url.set("https://www.github.com/Kashif-E/Simple-Paging/issues")
                }
                scm {
                    connection.set("https://github.com/Kashif-E/Simple-Paging.git")
                    developerConnection.set("https://github.com/Kashif-E")
                    url.set("https://github.com/Kashif-E/Simple-Paging")
                }

                developers {
                    developer {
                        id.set("Kashif-E")
                        name.set("Kashif Mehmood")
                        email.set("kashismails@gmail.com")
                    }
                }
                artifact(javaDocJar)

                signing{
                    useInMemoryPgpKeys(
                        System.getenv("GPG_PUBLIC_KEY"),
                        System.getenv("GPG_KEY_PASSWORD")
                    )
                    sign("simple_paging")
                }
            }
        }
    }
}

android {
    namespace = "io.github.kashif-e"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
