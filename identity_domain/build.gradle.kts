plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kover)
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }

        jvmTest.dependencies {
            implementation(libs.bundles.jvm.test)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
        packages("*.di", "*.entity", "*.exception")
    }
}
