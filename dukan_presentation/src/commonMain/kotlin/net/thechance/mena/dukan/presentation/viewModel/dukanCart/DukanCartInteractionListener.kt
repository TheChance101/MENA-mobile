package net.thechance.mena.dukan.presentation.viewModel.dukanCart

interface DukanCartInteractionListener {
    fun onBackClicked()
    fun onDukanClicked()
    fun onCheckoutClicked()
    fun onIncreaseItemQuantityClicked(productId: String)
    fun onDecreaseItemQuantityClicked(productId: String)
    fun onRemoveItemClicked(productId: String)
    fun onRetryLoadCartClicked()
}