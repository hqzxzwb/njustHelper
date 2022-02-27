plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    id("dev.icerock.mobile.multiplatform-resources")
}

multiplatformResources {
    multiplatformResourcesPackage = "com.njust.helper.shared"
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
    val ktorVersion = "2.0.0-beta-1"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("com.squareup.okio:okio:$okioVersion")
                implementation("dev.icerock.moko:resources:0.18.0")
                implementation(libs.koin.core)
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
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation(libs.koin.android)
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
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
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
