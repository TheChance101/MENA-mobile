package net.thechance.mena.wallet.presentation.screen.transaction_details


interface TransactionDetailsInteractionListener {
    fun onBackButtonClicked()
    fun onShareReceiptButtonClicked()
    fun onScreenShotCaptured(byteArray: ByteArray, fileName: String)
    fun onRefresh()
    fun onCaptureError()
}