package net.thechance.mena.wallet.presentation.screen.payment_result

interface PaymentResultInteractionListener {
    fun onBackClicked()
    fun onCancelClicked()
    fun onTryAgainClicked()
    fun onShowTransactionDetailsClicked()
}