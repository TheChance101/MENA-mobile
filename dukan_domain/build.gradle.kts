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
            implementation(libs.junit)
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
    filters {
        excludes {
            classes(
                "**.di.**",
                "**.entity.**",
                "**.exceptions.**",
                "**.repository.**",
                "**.util.**",
                "**.model.**"
            )
        }
    }
}