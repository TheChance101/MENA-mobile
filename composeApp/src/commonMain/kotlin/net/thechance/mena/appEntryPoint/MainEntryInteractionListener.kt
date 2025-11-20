package net.thechance.mena.appEntryPoint

interface MainEntryInteractionListener {
    fun onDeepLinkChange(deepLink: DeepLink)
    fun clearDeepLink()
    fun onBottomNavigationChanged(isShowed: Boolean)
    fun setActiveFeature(feature: Feature)
}