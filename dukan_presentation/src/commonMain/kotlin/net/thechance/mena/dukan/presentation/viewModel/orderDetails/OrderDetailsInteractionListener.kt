package net.thechance.mena.dukan.presentation.viewModel.orderDetails


interface OrderDetailsInteractionListener {
    fun onBackClicked()
    fun onAddressDeliveryClicked(address: OrderDetailsUiState.AddressDeliveryUiState)
    fun onRetryLoadingOrderDetailsClicked()
    fun onSnackBarDismissed()
}