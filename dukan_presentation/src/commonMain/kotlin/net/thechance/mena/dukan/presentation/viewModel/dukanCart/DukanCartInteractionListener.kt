package net.thechance.mena.dukan.presentation.viewModel.dukanCart

interface DukanCartInteractionListener {
    fun onBackClicked()
    fun onDukanClicked()
    fun onCheckoutClicked()
    fun onIncreaseItemQuantityClicked(productId: String, newQuantity: Int)
    fun onDecreaseItemQuantityClicked(productId: String, newQuantity: Int)
    fun onRemoveItemClicked(productId: String)
    fun onRetryLoadCartClicked()
    fun onDismissSnackBar()
}