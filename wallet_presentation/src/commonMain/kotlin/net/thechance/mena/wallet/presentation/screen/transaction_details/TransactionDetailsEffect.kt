package net.thechance.mena.wallet.presentation.screen.transaction_details

sealed interface TransactionDetailsEffect {
    data object NavigateBack : TransactionDetailsEffect
    data class ShareImage(
        val imageBytes: ByteArray,
        val fileName: String,
        val mimeType: String
    ) : TransactionDetailsEffect
    data object CaptureImage : TransactionDetailsEffect
}