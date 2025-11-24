package net.thechance.mena.trends.presentation.navigation

import kotlinx.serialization.Serializable

internal sealed interface Route {
    @Serializable
    data object Categories : Route

    @Serializable
    data object UpdateCategories : Route

    @Serializable
    data class TrendDetails(
        val trendId: String,
        val source: String
    ) : Route

    @Serializable
    enum class TrendSource { Home, MyTrends, Favorites }

    @Serializable
    data object ManageTrends : Route

    @Serializable
    data object MainContainer : Route

    @Serializable
    data object UploadTrend : Route

    @Serializable
    data class VideoDescription(val trendId: String) : Route

    @Serializable
    data class CategoriesPublish(val trendId: String, val description: String) : Route

    @Serializable
    data object Home : Route
}