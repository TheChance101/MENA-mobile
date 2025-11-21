plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kover)
    alias(libs.plugins.mockkery)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.room.sqlite.wrapper)
        }
        commonMain.dependencies {
            implementation(projects.faithDomain)
            implementation(projects.coreChatDomain)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.contacts.provider)
            implementation(libs.koin.core)

            implementation(libs.bundles.ktor)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(projects.identityDomain)

            implementation(libs.record.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.annotations.common)
            implementation(libs.ktor.client.mock)
            implementation(libs.assertk)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mokkery.core)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
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
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters.excludes {
        packages(
            "*.di",
            "*.dto" ,
            "*.database",
            "*.dataStore",
            "*.mapper",
            "*.utils",
            "*.network",
            "*.audio"
        )
    }
}