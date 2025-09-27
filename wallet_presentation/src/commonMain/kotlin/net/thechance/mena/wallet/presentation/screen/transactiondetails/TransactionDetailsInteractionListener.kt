package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap

interface TransactionDetailsInteractionListener {
    fun onBackButtonClicked()
    fun onShareReceiptButtonClicked()
    fun onScreenShotCaptured(imageBitmap: ImageBitmap, fileName: String)
    fun onRefresh()
}