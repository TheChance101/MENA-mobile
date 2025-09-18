package net.thechance.mena.dukan.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface DukanRoute {
    @Serializable
    object MainScreenRoute : DukanRoute
    @Serializable
    object CreateDukanScreenRoute : DukanRoute

    @Serializable
    object MyDukanScreenRoute : DukanRoute

    @Serializable
    data class PendingScreenRoute(val dukanName: String) : DukanRoute
}