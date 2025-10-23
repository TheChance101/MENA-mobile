package net.thechance.mena.trends.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {
    @Serializable
    data object Categories : Route

    @Serializable
    data object UpdateCategories : Route

    @Serializable
    data class ReelDetails(val reelId: String) : Route

    @Serializable
    data object ManageReels : Route

    @Serializable
    data object MainContainer: Route

    @Serializable
    data object UploadReel : Route

    @Serializable
    data class VideoDescription(val trendId : String) : Route

    @Serializable
    data class CategoriesPublish(val trendId : String, val description : String) : Route

    @Serializable
    data object Home : Route
}