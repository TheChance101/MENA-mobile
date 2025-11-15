package net.thechance.mena.faith.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    @Serializable
    data object SurRoute : Route

    @Serializable
    data class SurahDetailsRoute(
        val surahId: Int,
        val ayahNumber: Int? = null
    ) : Route

    @Serializable
    data object MainRoute : Route

    @Serializable
    data object BookmarksRoute : Route

    @Serializable
    data object CompassRoute : Route

    @Serializable
    data object CalibrateDeviceRoute : Route

    @Serializable
    data object PrayerTimeRoute : Route

    @Serializable
    data object NearbyMosquesRoute : Route

    @Serializable
    data class DownloadedSurScreen(
        val surahId: Int? = null
    ) : Route


    @Serializable
    data class DownloadedRecitersRoute(
        val surahId: Int? = null,
        val isCardsSwipable: Boolean = false,
    ) : Route

    @Serializable
    data class ReciterSearch(
        val surahId: Int? = null
    ) : Route
    
    @Serializable
    data object UserAddresses : Route

    @Serializable
    data class SearchRoute(
        val surahId: Int? = null,
        val surahName: String? = null
    ) : Route

    @Serializable
    data object CreateMosqueRoute : Route

    @Serializable
    data object UploadImageRoute : Route
}