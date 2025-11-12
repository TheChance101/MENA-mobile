import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mockkery)
    alias(libs.plugins.cocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DukanPresentation"
            isStatic = true
            freeCompilerArgs += listOf("-Xbinary=bundleId=net.thechance.mena.dukan.presentation")
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
            implementation(projects.dukanApi)
            implementation(projects.identityApi)
            implementation(projects.identityDomain)
            implementation(projects.walletApi)

            implementation(libs.squircle.shape)

            implementation(compose.runtime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.bundles.filekit)
            implementation(libs.krop.extensions.filekit)
            implementation(libs.krop.core)
            implementation(libs.navigation.compose)
            implementation(libs.androidx.paging.compose)

            // maps
            implementation(libs.maplibre.compose)
            implementation(libs.bundles.coil)
        }
        iosMain.dependencies {
            implementation(libs.bundles.coil)
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(kotlin("test-annotations-common"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mokkery.core)
            implementation(libs.androidx.paging.testing)
        }
    }
}

android {
    namespace = "net.thechance.mena.dukan.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters {
        excludes {
            packages("mena.dukan_presentation.generated.resources*")
            classes(
                "**.component.**",
                "**.di.**",
                "**.navigation.**",
                "**.screen.**",
                "**.util.**",
                "**.viewModel.cropImage.**"
            )
        }
    }
}