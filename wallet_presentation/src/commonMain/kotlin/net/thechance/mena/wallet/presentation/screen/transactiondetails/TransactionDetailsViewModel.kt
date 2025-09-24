package net.thechance.mena.wallet.presentation.screen.transactiondetails

import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.share_transaction_details_error_msg
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.screen.transactiondetails.TransactionDetailsScreenState.Transaction
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TransactionDetailsViewModel() :
    BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
        TransactionDetailsScreenState()
    ) , TransactionDetailsInteractionListener
{
    init {
        getTransactionDetails()
    }

    private fun getTransactionDetails(){
        TODO("Not yet implemented")
    }

    private fun onGetTransactionDetailsSuccess(){
        updateState { it.copy(transaction = UiState.Success(Transaction())) }
    }

    private fun onGetTransactionDetailsError(throwable: Throwable){
        updateState { it.copy(transaction = UiState.Error(throwable)) }
    }

    private fun onGetTransactionDetailsStart(){
        updateState { it.copy(transaction = UiState.Loading) }
    }

    override fun onBackBtnClicked() {
        sendEffect(TransactionDetailsEffect.NavigateBack)
    }

    override fun onShareReceiptBtnClicked() {
        TODO("Not yet implemented")
    }

    private fun onShareReceiptFinish(){
        updateState { it.copy(isShareReceiptLoading = false) }
    }

    private suspend fun onShareReceiptError(throwable: Throwable){
        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.share_transaction_details_error_msg,
            isSuccess = false
        )
    }

    private fun onShareReceiptStart(){
        updateState { it.copy(isShareReceiptLoading = true) }
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
}