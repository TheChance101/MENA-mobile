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
    data class PendingScreenRoute(val dukanName: String) : DukanRoute

    @Serializable
    data class ManageShelfScreenRoute(val shelfId: String, val shelfTitle: String) : DukanRoute

    @Serializable
    object CreateProductScreenRoute : DukanRoute

    @Serializable
    data class EditProductScreenRoute(
        val productId: String
    ) : DukanRoute

    @Serializable
    data class DukanDetails(val dukanId: String) : DukanRoute

    @Serializable
    object DukanCategoriesScreenRoute : DukanRoute

    @Serializable
    data class CheckoutScreenRoute(val dukanId: String) : DukanRoute

    @Serializable
    data class DukansScreenRoute(val categoryId: String, val categoryTitle: String) : DukanRoute

    @Serializable
    data class ShelfDetails(
        val shelfId: String,
        val shelfName: String,
        val dukanId: String
    ) : DukanRoute

    @Serializable
    data class ProductDetails(val productId: String, val dukanId: String) : DukanRoute

    @Serializable
    data class DukanCart(val dukanId: String) : DukanRoute

    @Serializable
    object SearchScreenRoute : DukanRoute

    @Serializable
    data object AddressesRoute : DukanRoute

    @Serializable
    data class DukanLocation(val latitude: Double, val longitude: Double) : DukanRoute

    @Serializable
    data class ConfirmPaymentScreenRoute(val transactionId: String, val dukanId: String) : DukanRoute
}