import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    val frameworkName = "CoreChatData"
    val coreChatDataXCFramework = XCFramework(frameworkName)

    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach { target ->
        target.binaries.framework(frameworkName) {
            baseName = "CoreChatData"
            isStatic = false
            coreChatDataXCFramework.add(this)
        }

    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.core)
            implementation(libs.androidx.work.runtime.ktx)

        }
        commonMain.dependencies {
            implementation(projects.coreChatDomain)
            implementation(libs.kotlin.serialization)
            implementation(libs.contacts.provider)
            implementation(libs.koin.core)
            implementation(libs.bundles.ktor)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.koin.core)
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
