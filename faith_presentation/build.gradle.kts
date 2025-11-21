import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.mockkery)
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
            baseName = "FaithPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.osmdroid.android)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(projects.faithDomain)
            implementation(projects.designSystem)
            implementation(projects.faithApi)
            implementation(projects.identityDomain)
            implementation(projects.identityApi)
            implementation(projects.coreChatApi)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.paging.runtime)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
            implementation(libs.bundles.filekit)
            implementation(libs.krop.extensions.filekit)
            implementation(libs.coil.gif)
            implementation(libs.krop.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier)

            // maps
            implementation(libs.maplibre.compose)
        }
        iosMain.dependencies {

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.mokkery.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.androidx.paging.testing)
        }
    }
}

android {
    namespace = "net.thechance.mena.faith.presentation"
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

    filters {
        includes {
            classes(
                "net.thechance.mena.faith.presentation.feature.quran.bookmark.BookmarkViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.sur.SurViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.surah.SurahViewModel",
                "net.thechance.mena.faith.presentation.feature.qiblah.compass.CompassViewModel",
                "net.thechance.mena.faith.presentation.feature.qiblah.calibratedevice.CalibrateDeviceViewModel",
                "net.thechance.mena.faith.presentation.feature.main.MainViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.search.SearchViewModel",
                "net.thechance.mena.faith.presentation.feature.prayertime.PrayerTimeViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen.SurahRecitersViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection.ReciterSelectionViewModel",
                "net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters.DownloadedRecitersViewModel",
                "*MapperKt",
            )
        }

        excludes {
            packages("net.thechance.mena.faith.presentation.util.extentions")
            annotatedBy("net.thechance.mena.faith.domain.annotation.KoverIgnore")
        }
    }
}