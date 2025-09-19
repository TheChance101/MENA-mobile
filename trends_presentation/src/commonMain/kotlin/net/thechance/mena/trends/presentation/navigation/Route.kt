package net.thechance.mena.trends.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Test: Route

    @Serializable
    data class ReelDetails(val reelId: String): Route

    @Serializable
    data object ManageReels: Route
}