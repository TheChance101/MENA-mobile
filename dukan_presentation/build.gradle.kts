import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.cocoapods)
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
            baseName = "DukanPresentation"
            isStatic = true
        }
    }

    cocoapods {
        summary = "DukanPresentation — internal KMP maps module for Dukan. Contains iOS-compatible map composables and shared location logic used across mobile modules."
        homepage = "https://github.com/TheChance101/MENA-mobile"
        version = "1.0"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "DukanPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(projects.dukanDomain)
            implementation(projects.designSystem)
            implementation(compose.runtime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.bundles.filekit)
            implementation(libs.krop.extensions.filekit)
            implementation(libs.krop.core)
            implementation(libs.navigation.compose)

            // maps
            implementation(libs.maplibre.compose)

            implementation(libs.turbine)
            implementation(libs.junit)
        }
        iosMain.dependencies {

        }

        jvmTest.dependencies{
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk)
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

dependencies {
    debugImplementation(compose.uiTooling)
}