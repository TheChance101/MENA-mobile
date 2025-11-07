import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val appVersionName =
    project.property("VERSION_MAJOR").toString() + "." + project.property("VERSION_MINOR")
        .toString()

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
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.designSystem)
            implementation(libs.bundles.koin)

            implementation(projects.identityApi)
            implementation(projects.identityPresentation)
            implementation(projects.identityData)
            implementation(projects.identityDomain)

            implementation(projects.coreChatApi)
            implementation(projects.coreChatPresentation)
            implementation(projects.coreChatDomain)
            implementation(projects.coreChatData)

            implementation(projects.dukanApi)
            implementation(projects.dukanPresentation)
            implementation(projects.dukanData)
            implementation(projects.dukanDomain)

            implementation(projects.faithApi)
            implementation(projects.faithPresentation)
            implementation(projects.faithData)
            implementation(projects.faithDomain)

            implementation(projects.walletApi)
            implementation(projects.walletPresentation)
            implementation(projects.walletData)
            implementation(projects.walletDomain)

            implementation(projects.trendsApi)
            implementation(projects.trendsPresentation)
            implementation(projects.trendsDomain)
            implementation(projects.trendsData)

            // File kit
            implementation(libs.bundles.filekit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "net.thechance.mena"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.thechance.mena"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = appVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "environment"

    productFlavors {
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            val baseUrl = localProperties.getProperty("BASE_URL_DEVELOPMENT", "")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            val baseUrl = localProperties.getProperty("BASE_URL_STAGING", "")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
        create("production") {
            dimension = "environment"
            val baseUrl = localProperties.getProperty("BASE_URL_PRODUCTION", "")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

tasks.register("generateEnvironmentXcconfig") {
    val developmentUrl = localProperties.getProperty("BASE_URL_DEVELOPMENT", "")
    val stagingUrl = localProperties.getProperty("BASE_URL_STAGING", "")
    val productionUrl = localProperties.getProperty("BASE_URL_PRODUCTION", "")
    val trendStorageAccessSecret = localProperties.getProperty("TRENDS_STORAGE_ACCESS_SECRET", "")
    val buildType = providers.environmentVariable("CONFIGURATION").orNull ?: ""

    val baseUrl = when {
        buildType.endsWith("Staging", ignoreCase = true) -> stagingUrl
        buildType.endsWith("Production", ignoreCase = true) -> productionUrl
        else -> developmentUrl
    }

    val outputFileProperty =
        project.layout.buildDirectory.file("generated/ios/environment.xcconfig")

    doLast {
        val outputFile = outputFileProperty.get().asFile
        outputFile.parentFile.mkdirs()

        outputFile.writeText(
            """
          SLASH = /
          BASE_URL = ${baseUrl.replace("//", "$(SLASH)$(SLASH)")}
          TRENDS_STORAGE_ACCESS_SECRET = $trendStorageAccessSecret
          CFBundleShortVersionString = $appVersionName
        """.trimIndent()
        )
    }
}

tasks.named("embedAndSignAppleFrameworkForXcode") {
    dependsOn(tasks.named("generateEnvironmentXcconfig"))
}