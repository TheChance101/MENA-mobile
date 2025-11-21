package net.thechance.mena.dukan.presentation.viewModel.orderDetails

sealed interface OrderDetailsEffect {
    object NavigateBack : OrderDetailsEffect
    data class NavigateToAddressOnMap(
        val startLatitude: Double,
        val startLongitude: Double,
        val endLatitude: Double,
        val endLongitude: Double
    ) : OrderDetailsEffect
}