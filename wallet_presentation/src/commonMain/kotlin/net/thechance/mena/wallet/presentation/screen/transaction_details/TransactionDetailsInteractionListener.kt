package net.thechance.mena.wallet.presentation.screen.transaction_details


interface TransactionDetailsInteractionListener {
    fun onBackButtonClicked()
    fun onShareReceiptButtonClicked(capture: suspend () -> Unit)
    fun onScreenShotCaptured(byteArray: ByteArray, fileName: String)
    fun onRefresh()
}