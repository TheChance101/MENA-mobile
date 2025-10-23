import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mockkery)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.dukanDomain)
            implementation(projects.identityDomain)
            implementation(libs.junit)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            // GeoCoder
            implementation(libs.bundles.geoCoder)
            // Ktor
            implementation(libs.bundles.ktor)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.auth)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(kotlin("test-annotations-common"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.bundles.geoCoder)
            implementation(libs.mokkery.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.bundles.geoCoder)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }
    filters {
        excludes {
            classes(
                "**.di.**",
                "**.util.**",
                "**.dto.**",
                "net.thechance.mena.dukan.data.util.wrapper.MobileGeocoderWrapper"
            )
        }
    }
}
android {
    namespace = "net.thechance.mena.dukan.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}