package net.thechance.mena.dukan.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface DukanRoute {
    @Serializable
    object MainScreenRoute : DukanRoute

    @Serializable
    object CategoryScreenRoute : DukanRoute

    @Serializable
    object CreateDukanRoute : DukanRoute

    @Serializable
    object MyDukanScreenRoute : DukanRoute

    @Serializable
    object RequestPendingScreenRoute : DukanRoute


}