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
            implementation(projects.dukanDomain)
            implementation(libs.junit)
        }
        iosMain.dependencies {

        }
    }
}