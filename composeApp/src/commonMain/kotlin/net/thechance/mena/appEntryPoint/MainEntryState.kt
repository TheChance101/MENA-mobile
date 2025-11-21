package net.thechance.mena.appEntryPoint

data class MainEntryState(
    val activeFeature: Feature = Feature.CHAT,
    val deepLink: DeepLink? = null,
    var showBottomNavigation: Boolean = true,
)

enum class Feature {
    CHAT, DUKAN, TREND, FAITH, PROFILE, WALLET
}