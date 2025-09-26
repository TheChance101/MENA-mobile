package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CaptureController
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class TransactionDetailsScreenState(
    val transactionDetailsUiState: UiState<TransactionDetailsUiState> = UiState.Idle,
    val shareReceipt: UiState<ImageBitmap> = UiState.Idle,
    val snackBar: SnackBarState = SnackBarState(),
    val captureController: CaptureController = CaptureController()
){
    data class TransactionDetailsUiState(
        val id: String = "",
        val amount: String = "",
        val date: String = "",
        val userName: String = "",
        val otherParty: String = "",
        val transactionType: Transaction.Type = Transaction.Type.ONLINE_PURCHASE,
        val transactionStatus: Transaction.Status = Transaction.Status.SUCCESS
    )
}