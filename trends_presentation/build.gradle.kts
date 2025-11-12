import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
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
            baseName = "TrendsPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui)
            implementation(libs.androidx.media3.exoplayer.dash)
        }
        commonMain.dependencies {
            implementation(projects.identityDomain)
            implementation(projects.trendsDomain)
            implementation(projects.trendsApi)
            implementation(projects.designSystem)
            implementation(libs.androidx.navigation.compose)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.koin.annotations)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.coil)
            implementation(libs.androidx.paging.runtime)
            implementation(libs.androidx.paging.compose)
            implementation(libs.resources)
            implementation(libs.kermit)
            implementation(libs.bundles.filekit)

            implementation(libs.kotlinx.datetime)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.bundles.coil)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.paging.testing)
        }
    }
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
    arg("KOIN_CONFIG_CHECK", "true")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}

tasks.matching {
    it.name.contains("compile") &&
            it.name.contains("Kotlin") &&
            !it.name.contains("ksp")
}.configureEach {
    dependsOn("kspCommonMainKotlinMetadata")
}

android {
    namespace = "net.thechance.mena.trends.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        val trendsStorageAccessSecret = localProperties.getProperty("TRENDS_STORAGE_ACCESS_SECRET", "")
        buildConfigField("String", "TRENDS_ACCESS_SECRET", "\"$trendsStorageAccessSecret\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures{
        buildConfig = true
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
            classes("*ViewModel")
        }

        excludes {
            classes(
                "**org.koin.ksp.generated**",
                "**.shared.**"
            )
        }
    }
}