package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap

interface TransactionDetailsInteractionListener {
    fun onBackBtnClicked()
    fun onShareReceiptBtnClicked()
    fun onScreenShotCaptured(imageBitmap: ImageBitmap)
    fun onRefresh()
    fun onSendToDeviceBtnClicked()
    fun onBottomSheetDismissRequest()
}