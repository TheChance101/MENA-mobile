plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    androidTarget()

    sourceSets {
        androidMain.dependencies {

        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.faithDomain)
        }
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "net.thechance.mena.faith.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
