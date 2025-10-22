import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
}

fun Project.gitTagVersion(): String {
    return try {
        val output = ByteArrayOutputStream()
        exec {
            workingDir = rootDir
            commandLine = listOf("git", "describe", "--tags", "--abbrev=0")
            standardOutput = output
            isIgnoreExitValue = true
        }

        val version = output.toString().trim()
        version.ifBlank { "0.0.1" }
    } catch (e: Exception) {
        logger.warn("⚠️ Không thể lấy git tag version: ${e.message}")
        "0.0.1"
    }
}

android {
    namespace = "com.inetkr.install_referrer"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        val gitVersion = project.gitTagVersion()
        buildConfigField("String", "SDK_VERSION", "\"$gitVersion\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    publishing {
        singleVariant("release")
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.integrity)
    implementation(libs.installreferrer)
    implementation(libs.play.services.ads.identifier)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.inetkr"
                artifactId = "install_referrer"
                version = project.gitTagVersion()

                pom {
                    name.set("install_referrer")
                    description.set("A lightweight Install Referrer SDK for Android")
                    url.set("https://github.com/inetkr/install_referrer")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("inetkr")
                            name.set("Kevin Lee")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/inetkr/install_referrer.git")
                        developerConnection.set("scm:git:ssh://github.com/inetkr/install_referrer.git")
                        url.set("https://github.com/inetkr/install_referrer")
                    }
                }
            }
        }
    }
}