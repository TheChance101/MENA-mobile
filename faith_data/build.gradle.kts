plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    iosArm64()
    androidTarget()
    iosSimulatorArm64()
    iosArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.room.sqlite.wrapper)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(projects.faithDomain)
            implementation(compose.components.resources)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.serialization.json)
            api(libs.koin.core)
            implementation(compose.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

kover.reports {
    verify {
        rule {
            minBound(0)
        }
    }
}

android {
    namespace = "net.thechance.mena.faith_data"
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
}

compose.resources {
    publicResClass = true
    packageOfResClass = "net.thechance.mena.faith_data"
}

listOf(
    "kspDebugKotlinAndroid",
    "kspReleaseKotlinAndroid"
).forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach {
        dependsOn(
            "generateComposeResClass",
            "generateExpectResourceCollectorsForCommonMain",
            "generateResourceAccessorsForCommonMain",
            "generateResourceAccessorsForAndroidMain",
            "generateActualResourceCollectorsForAndroidMain"
        )
        if (taskName == "kspDebugKotlinAndroid") {
            dependsOn("generateResourceAccessorsForAndroidDebug")
        }
    }
}

listOf(
    "kspKotlinIosSimulatorArm64",
    "kspKotlinIosX64",
    "kspKotlinIosArm64"
).forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach {
        dependsOn(
            "generateComposeResClass",
            "generateExpectResourceCollectorsForCommonMain",
            "generateResourceAccessorsForAppleMain",
            "generateResourceAccessorsForNativeMain",
            "generateResourceAccessorsForIosMain",
            "generateResourceAccessorsForCommonMain"
        )
        when (taskName) {
            "kspKotlinIosSimulatorArm64" -> dependsOn(
                "generateResourceAccessorsForIosSimulatorArm64Main",
                "generateActualResourceCollectorsForIosSimulatorArm64Main"
            )

            "kspKotlinIosX64" -> dependsOn(
                "generateResourceAccessorsForIosX64Main",
                "generateActualResourceCollectorsForIosX64Main"
            )

            "kspKotlinIosArm64" -> dependsOn(
                "generateResourceAccessorsForIosArm64Main",
                "generateActualResourceCollectorsForIosArm64Main"
            )
        }
    }
}