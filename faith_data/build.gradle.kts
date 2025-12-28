plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mockkery)
    alias(libs.plugins.ktorfit)
}

kotlin {
    androidTarget()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.room.sqlite.wrapper)
            implementation(libs.ktor.client.cio)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.koin.core)
            implementation(projects.faithDomain)
            implementation(compose.components.resources)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.identityDomain)
            implementation(libs.bundles.ktor)
            implementation(libs.androidx.datastore.preferences)
            api(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier)
            implementation(libs.bundles.ktorfit)
            implementation(libs.okio)
            implementation(libs.bundles.geoCoder)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mokkery.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.bundles.test)
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
        includes {
            classes(
                "*RepositoryImpl",
                "*MapperKt",
            )
        }

        excludes {
            annotatedBy("net.thechance.mena.faith.domain.annotation.KoverIgnore")
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
    addKsp(libs.androidx.room.compiler)
    addKsp(libs.ktorfit.ksp)
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

fun DependencyHandlerScope.addKsp(dependencyNotation: Any) {
    val targets = listOf(
        //"CommonMainMetadata",
        "Android",
        "AndroidTest",
        "IosX64",
        "IosX64Test",
        "IosArm64",
        "IosSimulatorArm64",
        "IosArm64Test",
        "IosSimulatorArm64Test"
    )

    targets.forEach { target ->
        runCatching {
            add(
                configurationName = "ksp$target",
                dependencyNotation = dependencyNotation
            )
        }
    }
}