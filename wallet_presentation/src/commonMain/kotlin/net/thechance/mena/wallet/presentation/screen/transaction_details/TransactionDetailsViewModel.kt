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
import net.thechance.mena.wallet.presentation.screen.transaction_details.args.TransactionDetailsArgs
import net.thechance.mena.wallet.presentation.utils.MimeType
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionDetailsViewModel(
    @Provided private val transactionDetailsArgs: TransactionDetailsArgs,
    @Provided val transactionRepository: TransactionRepository,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
    TransactionDetailsScreenState()), TransactionDetailsInteractionListener {

    val id = transactionDetailsArgs.id

    init {
        getTransactionDetails()
    }

    private fun getTransactionDetails() {
        tryToExecute(
            callee = {
                transactionRepository.getTransactionById(
                    transactionId = Uuid.parse(id)
                )
            },
            onSuccess = ::onGetTransactionDetailsSuccess,
            onError = ::onGetTransactionDetailsError,
            onStart = ::onGetTransactionDetailsStart,
            dispatcher = dispatcher
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
        sendEffect(
            TransactionDetailsEffect.ShareImage(
                imageBytes = byteArray,
                fileName = "$fileName.png",
                mimeType = MimeType.PNG
            )
        )
        stopButtonLoading()
    }

    override fun onRefresh() {
        updateState { it.copy(isLoading = true, errorState = null) }
        getTransactionDetails()
    }

    override suspend fun onCaptureError() {
        showSnackBar(
            title = stringProvider.getString(Res.string.error),
            message = stringProvider.getString(Res.string.share_transaction_details_error_msg),
            isSuccess = false)
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    title = title,
                    message = message,
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
}