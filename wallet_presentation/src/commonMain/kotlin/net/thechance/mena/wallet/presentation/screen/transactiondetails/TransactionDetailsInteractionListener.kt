package net.thechance.mena.wallet.presentation.screen.transactiondetails

interface TransactionDetailsInteractionListener {
    fun onBackBtnClicked()
    fun onShareReceiptBtnClicked()
    fun onRefresh()
    fun onSendToDeviceBtnClicked()
    fun onBottomSheetDismissRequest()
}