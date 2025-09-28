package net.thechance.mena.wallet.presentation.screen.transactiondetails


interface TransactionDetailsInteractionListener {
    fun onBackButtonClicked()
    fun onShareReceiptButtonClicked()
    fun onScreenShotCaptured(byteArray: ByteArray, fileName: String)
    fun onRefresh()
}