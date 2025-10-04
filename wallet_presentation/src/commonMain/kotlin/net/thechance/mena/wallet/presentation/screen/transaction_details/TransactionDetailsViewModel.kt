package net.thechance.mena.wallet.presentation.screen.transaction_details

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionDetailsViewModel(
    @Provided private val transactionId: String,
    @Provided val transactionRepository: TransactionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
    TransactionDetailsScreenState()), TransactionDetailsInteractionListener {

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

    override fun onShareReceiptButtonClicked() {
        startButtonLoading()
        sendEffect(TransactionDetailsEffect.CaptureImage)
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun onScreenShotCaptured(byteArray: ByteArray, fileName: String) {
        sendEffect(TransactionDetailsEffect.ShareImage(
            imageBytes = byteArray,
            fileName = "$fileName.png",
            mimeType = IMAGE_TYPE
            )
        )
        stopButtonLoading()
    }

    override fun onRefresh() {
        updateState { it.copy(isLoading = true, errorState = null) }
        getTransactionDetails()
    }

    override fun onCaptureError() {
        sendEffect(TransactionDetailsEffect.showErrorSnackBar)
    }

    suspend fun showSnackBar(
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

    private fun stopButtonLoading() {
        updateState { it.copy(isShareReceiptBtnLoading = false) }
    }

    private fun startButtonLoading() {
        updateState { it.copy(isShareReceiptBtnLoading = true) }
    }

    private companion object {
        const val IMAGE_TYPE = "image/png"
    }
}