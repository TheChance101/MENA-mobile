import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
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
            baseName = "IdentityPresentation"
            isStatic = true
        }
    }

    cocoapods {
        summary = "IdentityPresentation — internal KMP maps module for Identity. Contains iOS-compatible map composables and shared location logic used across mobile modules."
        homepage = "https://github.com/TheChance101/MENA-mobile"
        version = "1.0"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "IdentityPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        androidUnitTest.dependencies{
            implementation(libs.kotlin.test)
            implementation(libs.bundles.jvm.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
        }
        commonMain.dependencies {
            implementation(projects.identityApi)
            implementation(projects.identityDomain)
            implementation(projects.designSystem)
            implementation(libs.bundles.filekit)

            implementation(libs.squircle.shape)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.voyager)
            implementation(libs.bundles.coil)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.ui.backhandler)


            // maps
            implementation(libs.maplibre.compose)
            implementation(libs.kotlinx.datetime)

            // QR code
            implementation(libs.qrose)
        }
    }
}

android {
    namespace = "net.thechance.mena.identity.presentation"
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

    filters.includes {
        classes("*ScreenModel*")
    }

    // todo: remove the equivalent tests after adding them or increasing their coverage
    filters.excludes {
        packages(
            "*.register",
            "*base",
            "*forget_password"
        )
    }
}