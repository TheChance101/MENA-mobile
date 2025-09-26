package net.thechance.mena.trends.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {
    @Serializable
    data object Categories : Route

    @Serializable
    data object Trends : Route

    @Serializable
    data class ReelDetails(val reelId: String) : Route

    @Serializable
    data object ManageReels : Route

    @Serializable
    data object MainContainer: Route

    @Serializable
    data class VideoDescription(val trendId : String) : Route
}