import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            api(libs.koin.annotations)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.mokkery.core)
        }
    }
    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }
}

android {
    namespace = "net.thechance.mena.wallet.domain"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
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
                "**.exceptions.**",
                "**.repository.**",
                "**.generated.**",
                "**.entity.**",
                "**.model.**",
                "**.utils.**",
            )
        }
    }
}
