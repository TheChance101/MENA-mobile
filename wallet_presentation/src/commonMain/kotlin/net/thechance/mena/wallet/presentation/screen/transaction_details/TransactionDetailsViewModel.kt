package net.thechance.mena.wallet.presentation.screen.transaction_details

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.share_transaction_details_error_msg
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.ImageSharer
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionDetailsViewModel(
    @Provided val imageSharer: ImageSharer,
    @Provided val transactionRepository: TransactionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @Provided private val transactionId: String,
) : BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>
    (TransactionDetailsScreenState()), TransactionDetailsInteractionListener {

    init {
        getTransactionDetails()
    }

    private fun getTransactionDetails() {
        tryToExecute(
            callee = {
                transactionRepository.getTransactionById(
                    transactionId = Uuid.parse(transactionId)
                )
            },
            onSuccess = ::onGetTransactionDetailsSuccess,
            onError = ::onGetTransactionDetailsError,
            onStart = ::onGetTransactionDetailsStart,
            dispatcher = ioDispatcher
        )
    }

    private fun onGetTransactionDetailsSuccess(transaction: Transaction) {
        updateState {
            it.copy(isLoading = false, transactionDetailsUiState = transaction.toUi())
        }
    }

    private fun onGetTransactionDetailsError(errorState: ErrorState) {
        updateState { it.copy(isLoading = false, errorState = errorState) }
    }

    private fun onGetTransactionDetailsStart() {
        updateState { it.copy(isLoading = true) }
    }

    override fun onBackButtonClicked() {
        sendEffect(TransactionDetailsEffect.NavigateBack)
    }

    override fun onShareReceiptButtonClicked(capture: suspend () -> Unit) {
        tryToExecute(
            callee = { capture() },
            onSuccess = ::onShareReceiptSuccess,
            onError = ::onShareReceiptError,
            onStart = ::onShareReceiptStart,
            dispatcher = ioDispatcher
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun onScreenShotCaptured(byteArray: ByteArray, fileName: String) {
        tryToExecute(
            callee = {
                imageSharer.shareImage(
                    imageBytes = byteArray,
                    fileName = "$fileName.png",
                    mimeType = IMAGE_TYPE
                )
            },
            onSuccess = ::onScreenShotCapturedSuccess,
            onError = ::onShareReceiptError,
            onStart = ::onShareReceiptStart,
            dispatcher = ioDispatcher
        )
    }

    private fun onScreenShotCapturedSuccess(x: Unit) {
        updateState { it.copy(isShareReceiptBtnLoading = false) }
    }

    private fun onShareReceiptSuccess(x: Unit) {
        updateState { it.copy(isShareReceiptBtnLoading = false) }
    }

    private suspend fun onShareReceiptError(errorState: ErrorState) {
        updateState { it.copy(isShareReceiptBtnLoading = false) }
        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.share_transaction_details_error_msg,
            isSuccess = false
        )
    }

    private fun onShareReceiptStart() {
        updateState { it.copy(isShareReceiptBtnLoading = true) }
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
        updateState { it.copy(isLoading = true, errorState = null) }
        getTransactionDetails()
    }

    private companion object {
        const val IMAGE_TYPE = "image/png"
    }
}