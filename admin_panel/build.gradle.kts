import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.mockkery)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.material3)

            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.collection)

            implementation(projects.designSystem)

            // datetime
            implementation(libs.kotlinx.datetime)

            // Koin
            implementation(libs.bundles.koin)
            api(libs.koin.annotations)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            // Network
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.ktorfit)

            // Coil
            implementation(libs.bundles.coil)

            //Navigation
            implementation(libs.androidx.navigation.compose)

            //settings
            implementation(libs.multiplatform.settings)

            //flow settings
            implementation(libs.multiplatform.settings.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mokkery.core)
            implementation(libs.mockk)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)

                // map
                implementation(libs.jxmapviewer)

                // Batik
                implementation(libs.batik.transcoder)
                implementation(libs.batik.codec)            }
        }
    }
}

buildkonfig {
    packageName = "net.thechance.mena.admin_panel"

    defaultConfigs {
        val baseUrl = localProperties.getProperty("BASE_URL", "")
        buildConfigField(type = Type.STRING, name = "BASE_URL", value = baseUrl)
    }
}

dependencies {
    addKsp(libs.koin.ksp.compiler)
    addKsp(libs.ktorfit.ksp)
}

compose.desktop {
    application {
        mainClass = "net.thechance.mena.admin_panel.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "net.thechance.mena.admin_panel"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(project.file("src/desktopMain/resources/mena_logo.ico"))
            }
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/mena_logo.icns"))
            }
            linux {
                iconFile.set(project.file("src/desktopMain/resources/mena_logo.png"))
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "net.thechance.mena.admin_panel.resources"
    generateResClass = always
}


kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }
    filters {
        includes {
            classes("net.thechance.mena.admin_panel.presentation..*.*ViewModel")
            classes("net.thechance.mena.admin_panel.data.repository.*")
        }
        excludes {
            classes("**org.koin.ksp.generated**")
        }
    }
}

fun DependencyHandlerScope.addKsp(dependencyNotation: Any) {
    val targets = listOf(
        "CommonMainMetadata",
        "Desktop",
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