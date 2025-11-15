@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import net.thechance.mena.dukan.presentation.viewModel.base.BaseViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderDetailsViewModel(
    // Todo Repository Injection
) : OrderDetailsInteractionListener,
    BaseViewModel<OrderDetailsUiState, OrderDetailsEffect>(
        initialState = OrderDetailsUiState()
    ) {

    fun loadOrderDetails(orderId: Uuid) {
        // Todo load order details by id
    }

    override fun onBackClicked() {
        emitEffect(OrderDetailsEffect.NavigateBack)
    }

    override fun onAddressDeliveryClicked() {
        // todo get real coordinates from order details
        emitEffect(OrderDetailsEffect.NavigateToAddressOnMap(
            startLatitude = 0.0,
            startLongitude = 0.0,
            endLatitude = 0.0,
            endLongitude = 0.0
        ))
    }
}