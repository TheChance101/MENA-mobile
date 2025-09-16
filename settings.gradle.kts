rootProject.name = "MENA"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":design_system")
include(":identity_presentation")
include(":core_chat_presentation")
include(":wallet_presentation")
include(":faith_presentation")
include(":dukan_presentation")
include(":trends_presentation")
include(":identity_api")
include(":core_chat_api")
include(":wallet_api")
include(":faith_api")
include(":dukan_api")
include(":trends_api")
include(":identity_domain")
include(":core_chat_domain")
include(":wallet_domain")
include(":faith_domain")
include(":dukan_domain")
include(":trends_domain")
include(":identity_data")
include(":core_chat_data")
include(":wallet_data")
include(":dukan_data")
include(":faith_data")
include(":trends_data")
