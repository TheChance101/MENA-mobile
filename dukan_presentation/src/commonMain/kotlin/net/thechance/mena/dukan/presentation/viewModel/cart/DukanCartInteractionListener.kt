package net.thechance.mena.dukan.presentation.viewModel.cart

interface DukanCartInteractionListener {
    fun onBackClick()
    fun onDukanDetailsClick(dukanId: String)
    fun onCheckoutClick(dukanId: String)
    fun onIncreaseItemQuantityClick(cartItemId: String)
    fun onDecreaseItemQuantityClick(cartItemId: String)
    fun onRemoveItemClick(cartItemId: String)
    fun onRetryLoadCartClick()
}