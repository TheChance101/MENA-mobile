package net.thechance.mena.dukan.presentation.viewModel.dukanCart

interface DukanCartInteractionListener {
    fun onBackClicked()
    fun onDukanClicked()
    fun onCheckoutClicked()
    fun onIncreaseItemQuantityClicked(cartItemId: String)
    fun onDecreaseItemQuantityClicked(cartItemId: String)
    fun onRemoveItemClicked(cartItemId: String)
    fun onRetryLoadCartClicked()
}