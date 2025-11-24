package net.thechance.mena.dukan.presentation.viewModel.dukanDetails

sealed class DukanDetailsEffects {
    object NavigateBackWithDukanId : DukanDetailsEffects()
    data class NavigateToViewAllShelfProducts(
        val id: String,
        val name: String,
        val dukanId: String
    ) : DukanDetailsEffects()

    data class NavigateToViewDukanOnMap(val latitude: Double, val longitude: Double) :
        DukanDetailsEffects()

    data class NavigateToProductDetails(val productId: String,val dukanId: String) : DukanDetailsEffects()

    data class NavigateToCart(val dukanId: String) : DukanDetailsEffects()
}