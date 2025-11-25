package net.thechance.mena.dukan.presentation.viewModel.checkout

interface CheckoutInteractionListener {
    fun onBackClicked()
    fun onConfirmOrderClicked()
    fun onChangeLocationClicked()
    fun onSnackBarDismissed()
    fun onRetryClicked()
}