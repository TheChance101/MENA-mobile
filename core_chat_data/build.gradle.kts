plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    alias(libs.plugins.mockkery)
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.annotations.common)
            implementation(libs.ktor.client.mock)
            implementation(libs.assertk)
            implementation(libs.kotlinx.coroutines.test)
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

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters.excludes {
        packages(
            "*.di",
            "*.dto",
        )
    }
}