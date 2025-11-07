package net.thechance.mena.appEntryPoint

interface MainEntryInteractionListener {

    fun onDeepLinkChange(deepLink: DeepLink)

    fun clearDeepLink()

    fun setActiveFeature(feature: Feature)
}