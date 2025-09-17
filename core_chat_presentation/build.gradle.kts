import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreChatPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.koin.workmanager)
            implementation(libs.androidx.work.runtime.ktx)
        }
        commonMain.dependencies {
            implementation(projects.coreChatApi)
            implementation(projects.coreChatDomain)
            implementation(projects.designSystem)

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            //coil
            implementation(libs.coil.compose)

            //data time
            implementation(libs.kotlinx.datetime)

            //implementation(libs.coil.compose.core)
            implementation(libs.coil.network.ktor)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Paging 3
            implementation(libs.paging.compose.common)
            implementation(libs.paging.common)
            // Koin
            implementation(libs.bundles.koin.compose)

            //permission
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions.compose)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "net.thechance.mena.core_chat.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    debugImplementation(compose.uiTooling)
}