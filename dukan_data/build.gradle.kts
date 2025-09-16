plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {

        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(projects.dukanDomain)
            implementation(libs.junit)
        }
        iosMain.dependencies {

        }
    }
}