package net.thechance.mena.wallet.presentation.screen.payment_result

interface PaymentResultInteractionListener {
    fun onBackClicked()
    fun onTryAgainClicked()
    fun onCloseClicked()
    fun onShowTransactionDetailsClicked()
}
