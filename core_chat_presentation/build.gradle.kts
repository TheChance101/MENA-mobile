import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
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
            baseName = "CoreChatPresentation"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(projects.coreChatApi)
            implementation(projects.coreChatDomain)

            implementation(projects.walletApi)
            implementation(projects.walletDomain)

            implementation(projects.faithApi)
            implementation(projects.faithDomain)

            implementation(projects.identityDomain)

            implementation(projects.designSystem)
            implementation(projects.dukanApi)

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            //coil
            implementation(libs.bundles.coil)

            //data time
            implementation(libs.kotlinx.datetime)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Paging 3
            implementation(libs.androidx.paging.runtime)
            implementation(libs.androidx.paging.compose)

            // Koin
            implementation(libs.bundles.koin.compose)

            //permission
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions.compose)

            // back handler
            implementation(libs.compose.ui.backhandler)

            //peekaboo
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)

            implementation(libs.bundles.filekit)
        }
        iosMain.dependencies {
            implementation(libs.bundles.coil)
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.assertk)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.mokkery.core)
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

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters {
        includes {
            classes(
                "*SyncContactsViewModel*",
                "*ContactsViewModel*",
                "*ChatViewModel*",
                "*HomeViewModel*"
            )
        }
    }
}