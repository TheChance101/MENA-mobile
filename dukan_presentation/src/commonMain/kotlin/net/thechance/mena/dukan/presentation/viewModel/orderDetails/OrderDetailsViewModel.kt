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
}