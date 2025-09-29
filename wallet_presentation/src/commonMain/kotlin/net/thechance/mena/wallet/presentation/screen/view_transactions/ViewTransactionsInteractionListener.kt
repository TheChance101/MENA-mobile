package net.thechance.mena.wallet.presentation.screen.view_transactions

interface ViewTransactionsInteractionListener {
    fun onBackClicked()
    fun onShareClicked(pdfBytes: ByteArray, fileName: String)
}