plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.squareup.sqldelight")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
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
            export(libs.koin.core)
        }
    }
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "shared"
            export(libs.koin.core)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.json)
                implementation(libs.okio)
                implementation(libs.moko.resources)
                api(libs.koin.core)
                implementation(libs.sqldelight.coroutine)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("com.github.hqzxzwb:koruksp:0.12.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core)
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

dependencies {
    val korukspProcessor = "com.github.hqzxzwb:koruksp-processor:0.12.1"
    add("kspIosArm64", korukspProcessor)
    add("kspIosX64", korukspProcessor)
    add("kspIosSimulatorArm64", korukspProcessor)
}
