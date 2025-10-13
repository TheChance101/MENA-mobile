package net.thechance.mena.faith.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {

    @Serializable
    data object SurRoute : Route

    @Serializable
    data class SurahDetailsRoute(
        val surahId: Int,
        val surahName: String,
        val ayahNumber: Int? = null
    ) : Route

    @Serializable
    data object MainRoute : Route

    @Serializable
    data object BookmarksRoute : Route

    @Serializable
    data object CalibrateDeviceRoute : Route

    @Serializable
    data class SearchRoute(
        val surahId: Int? = null,
        val surahName: String? = null
    ) : Route
}