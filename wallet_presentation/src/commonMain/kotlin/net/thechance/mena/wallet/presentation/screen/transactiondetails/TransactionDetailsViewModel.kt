package net.thechance.mena.wallet.presentation.screen.transactiondetails

import androidx.compose.ui.graphics.ImageBitmap
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.toByteArray
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.share_transaction_details_error_msg
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.base.UiState
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
    @Provided val transactionRepository: TransactionRepository
) : BaseViewModel<TransactionDetailsScreenState, TransactionDetailsEffect>(
        TransactionDetailsScreenState()
    ), TransactionDetailsInteractionListener {
    init {
        getTransactionDetails()
    }

    private fun getTransactionDetails() {
        tryToExecute(
            callee = { transactionRepository.getTransactionDetails(Uuid.random()) },
            onSuccess = ::onGetTransactionDetailsSuccess,
            onError = ::onGetTransactionDetailsError,
            onStart = ::onGetTransactionDetailsStart,
        )
    }

    private fun onGetTransactionDetailsSuccess(transaction: Transaction) {
        updateState { it.copy(transactionDetailsUiState = UiState.Success(transaction.toUi())) }
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
        updateState { it.copy(shareReceipt = UiState.Loading) }
        state.value.captureController.capture()
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun onScreenShotCaptured(imageBitmap: ImageBitmap, fileName: String) {
        val byteArray = imageBitmap.toByteArray(CompressionFormat.PNG, 100)
        updateState {
            it.copy(
                shareReceipt = UiState.Success(imageBitmap),
            )
        }
        tryToExecute(
            callee = {
                imageSharer.shareImage(
                    imageBytes = byteArray,
                    fileName = "$fileName.png",
                    mimeType = IMAGE_TYPE
                )
            },
            onSuccess = { updateState { it.copy(shareReceipt = UiState.Idle) } },
            onError = ::onShareReceiptError,
            onStart = ::onShareReceiptStart,
        )
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
    }

    private companion object {
        const val IMAGE_TYPE = "image/png"
    }
}