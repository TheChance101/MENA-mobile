import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
        }
        commonMain.dependencies {
            implementation(projects.dukanDomain)
            implementation(libs.junit)
            // GeoCoder + GeoLocation
            implementation(libs.bundles.geoLocation)
            implementation(libs.bundles.geoCoder)
        }
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "net.thechance.mena.dukan.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}