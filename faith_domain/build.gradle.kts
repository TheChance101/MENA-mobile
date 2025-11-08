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
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(projects.identityDomain)
            implementation(libs.kotlinx.coroutines.core)

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
        includes {
            classes(
                "*UseCase"
            )
        }

        excludes {
            annotatedBy("net.thechance.mena.faith.domain.annotation.KoverIgnore")
        }
    }
}
