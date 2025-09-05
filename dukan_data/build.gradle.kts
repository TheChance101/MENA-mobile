plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val desktopMain by getting
        androidMain.dependencies {

        }
        commonMain.dependencies {
            implementation(projects.dukanDomain)
        }
        iosMain.dependencies {

        }
        desktopMain.dependencies {

        }
    }
}