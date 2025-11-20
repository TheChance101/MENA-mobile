@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.viewModel.orderDetails

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface OrderDetailsInteractionListener {
    fun onBackClicked()
    fun onAddressDeliveryClicked(address: OrderDetailsUiState.AddressDeliveryUiState)
    fun onRetryLoadingOrderDetailsClicked(orderId: Uuid)
    fun onSnackBarDismissed()
}