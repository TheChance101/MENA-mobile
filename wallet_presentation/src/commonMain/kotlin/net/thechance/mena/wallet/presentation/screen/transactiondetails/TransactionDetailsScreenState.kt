package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CaptureController
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState

data class TransactionDetailsScreenState(
    val transactionDetailsUiState: UiState<TransactionDetailsUiState> = UiState.Idle,
    val shareReceipt: UiState<ImageBitmap> = UiState.Idle,
    val snackBar: SnackBarState = SnackBarState(),
    val isBottomSheetVisible: Boolean = false,
    val captureController: CaptureController = CaptureController()
){
    data class TransactionDetailsUiState(
        val id: String = "",
        val amount: String = "",
        val date: String = "",
        val userName: String = "",
        val otherParty: String = "",
        val transactionType: TransactionType = TransactionType.PAY,
        val transactionStatus: TransactionStatus = TransactionStatus.FAILED
    )

    enum class TransactionType{
        PAY,
        SEND,
        RECEIVE
    }

    enum class TransactionStatus{
        FAILED,
        SUCCESS
    }
}