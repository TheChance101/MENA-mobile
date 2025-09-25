package net.thechance.mena.wallet.presentation.screen.transactiondetails

import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.share_transaction_details_error_msg
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.TransactionDetailsUiState
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TransactionDetailsViewModel() :
    BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
        TransactionDetailsScreenState()
    ), TransactionDetailsInteractionListener {
    init {
        getTransactionDetails()
    }

    private fun getTransactionDetails() {
        TODO("Not yet implemented")
    }

    private fun onGetTransactionDetailsSuccess() {
        updateState { it.copy(transactionDetailsUiState = UiState.Success(TransactionDetailsUiState())) }
    }

    private fun onGetTransactionDetailsError(throwable: Throwable) {
        updateState { it.copy(transactionDetailsUiState = UiState.Error(throwable)) }
    }

    private fun onGetTransactionDetailsStart() {
        updateState { it.copy(transactionDetailsUiState = UiState.Loading) }
    }

    override fun onBackBtnClicked() {
        sendEffect(TransactionDetailsEffect.NavigateBack)
    }

    override fun onShareReceiptBtnClicked() {
        TODO("Not yet implemented")
    }

    private fun onShareReceiptSuccess() {
        updateState {
            it.copy(
                shareReceipt = UiState.Success(Unit),
                isBottomSheetVisible = true
            )
        }
    }

    private suspend fun onShareReceiptError(throwable: Throwable) {
        updateState { it.copy(shareReceipt = UiState.Error(throwable)) }
        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.share_transaction_details_error_msg,
            isSuccess = false
        )
    }

    private fun onShareReceiptStart() {
        updateState { it.copy(shareReceipt = UiState.Loading) }
    }

    private suspend fun showSnackBar(
        titleRes: StringResource,
        messageRes: StringResource,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    titleRes = titleRes,
                    messageRes = messageRes,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState ->
            oldState.copy(
                snackBar = oldState.snackBar.copy(isVisible = false)
            )
        }
    }

    override fun onRefresh() {
        getTransactionDetails()
    }

    override fun onSendToDeviceBtnClicked() {
        TODO("Not yet implemented")
    }

    override fun onBottomSheetDismissRequest() {
        updateState { it.copy(isBottomSheetVisible = false) }
    }
}