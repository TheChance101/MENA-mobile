plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.identityDomain)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.ktor)
            implementation(libs.ktor.client.cio)
            implementation(libs.koin.core)
            implementation(libs.multiplatform.settings)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.bundles.jvm.test)
        }
    }
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters.excludes {
        packages("*.di", "*.dto", "*.utils")
    }
}
