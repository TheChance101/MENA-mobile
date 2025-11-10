import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mockkery)
    alias(libs.plugins.room)
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
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.room.sqlite.wrapper)
        }

        commonMain.dependencies {
            implementation(projects.trendsDomain)
            implementation(libs.koin.core)
            api(libs.koin.annotations)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bundles.ktor)
            implementation(libs.kermit)
            implementation(projects.identityDomain)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.bundles.test)
            implementation(libs.turbine)
        }
    }
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    addKsp(libs.androidx.room.compiler)
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "net.thechance.mena.trends.data"
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
                "**.repository.**",
                "**.mapper.**",
            )
        }

        excludes {
            classes("**org.koin.ksp.generated**")
        }
    }
}

fun DependencyHandlerScope.addKsp(dependencyNotation: Any) {
    val targets = listOf(
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