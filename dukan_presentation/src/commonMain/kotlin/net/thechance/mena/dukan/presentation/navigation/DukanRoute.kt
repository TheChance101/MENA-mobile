package net.thechance.mena.dukan.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface DukanRoute {
    @Serializable
    object MainScreenRoute : DukanRoute

    @Serializable
    object CreateDukanScreenRoute : DukanRoute

    @Serializable
    object CreateShelfScreenRoute : DukanRoute

    @Serializable
    object ApprovedDukanScreenRoute : DukanRoute

    @Serializable
    object MyDukanScreenRoute : DukanRoute

    @Serializable
    data class PendingScreenRoute(
        val dukanName: String,
    ) : DukanRoute

    @Serializable
    data class ManageShelfScreenRoute(val shelfId: String, val shelfTitle: String) : DukanRoute
}