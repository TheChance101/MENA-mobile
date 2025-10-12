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
    object ManageDukanScreenRoute : DukanRoute

    @Serializable
    object MyDukanScreenRoute : DukanRoute

    @Serializable
    data class PendingScreenRoute(val dukanName: String) : DukanRoute

    @Serializable
    data class ManageShelfScreenRoute(val shelfId: String, val shelfTitle: String) : DukanRoute

    @Serializable
    object CreateProductScreenRoute : DukanRoute

    @Serializable
    data class DukanDetails(val dukanId: String) : DukanRoute

    @Serializable
    data class ShelfDetails(val shelfId: String, val shelfName: String) : DukanRoute

    @Serializable
    object DukanCategoriesScreenRoute : DukanRoute

    @Serializable
    data class DukansScreenRoute(val categoryId: String, val categoryTitle: String) : DukanRoute
}