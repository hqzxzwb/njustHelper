plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.squareup.sqldelight")
}

multiplatformResources {
    multiplatformResourcesPackage = "com.njust.helper.shared"
}

sqldelight {
    database("CourseQueryDatabaseInternal") {
        packageName = "com.njust.helper.shared.database.coursequery"
        sourceFolders = listOf("CourseQueryDatabase")
    }
}

kotlin {
    android()
    ios {
        binaries.framework {
            baseName = "shared"
        }
    }
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "shared"
        }
    }

    val okioVersion = "3.0.0"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.json)
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation(libs.moko.resources)
                implementation(libs.koin.core)
                implementation(libs.sqldelight.coroutine)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("com.squareup.okio:okio-fakefilesystem:$okioVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core:1.6.0")
                implementation(libs.ktor.okhttp)
                implementation(libs.koin.android)
                implementation(libs.sqldelight.android)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.sqldelight.native)
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}
