plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(projects.coreChatDomain)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.contacts.provider)
            implementation(libs.koin.core)

            implementation(libs.bundles.ktor)
            implementation(libs.androidx.datastore.preferences)

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "net.thechance.mena.core_chat.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
