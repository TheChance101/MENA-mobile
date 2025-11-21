@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.orderDetails.OrderDetailsUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object PreviewOrderDetailsInteractionListener: OrderDetailsInteractionListener {
    override fun onBackClicked() {}
    override fun onAddressDeliveryClicked(address: OrderDetailsUiState.AddressDeliveryUiState) {}
    override fun onRetryLoadingOrderDetailsClicked(orderId: Uuid) {}
    override fun onSnackBarDismissed() {}
}