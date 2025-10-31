package net.thechance.mena.dukan.presentation.viewModel.dukanCart

interface DukanCartInteractionListener {
    fun onBackClick()
    fun onDukanDetailsClick()
    fun onCheckoutClick()
    fun onIncreaseItemQuantityClick(cartItemId: String)
    fun onDecreaseItemQuantityClick(cartItemId: String)
    fun onRemoveItemClick(cartItemId: String)
    fun onRetryLoadCartClick()
}