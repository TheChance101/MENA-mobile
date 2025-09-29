package net.thechance.mena.wallet.presentation.screen.view_transactions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.error_no_data
import mena.wallet_presentation.generated.resources.file_shared_successfully
import mena.wallet_presentation.generated.resources.something_went_wrong
import mena.wallet_presentation.generated.resources.success
import net.thechance.mena.wallet.domain.repository.BitmapRepo
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.utils.PdfShare
import net.thechance.mena.wallet.presentation.utils.toImageBitmap
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ViewTransactionsViewModel(
    @Provided private val bitmapRepo: BitmapRepo,
    @Provided private val pdfShare: PdfShare,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ViewTransactionsScreenState, ViewTransactionsEffect>(ViewTransactionsScreenState()),
    ViewTransactionsInteractionListener {

    init {
        getBitmap()
    }

    fun getBitmap() {
        tryToExecute(
            onStart = ::onGetBitmapStart,
            callee = { bitmapRepo.getBitmapData() },
            onSuccess = ::onGetBitmapSuccess,
            onError = ::onGetBitmapError,
            dispatcher = ioDispatcher
        )
    }

    private fun onGetBitmapStart() {
        updateState { it.copy(isLoading = true) }
    }

    private suspend fun onGetBitmapSuccess(data: ByteArray) {
        val imageBitmap = data.toImageBitmap()
        if (imageBitmap != null) {
            updateState {
                it.copy(
                    isLoading = false,
                    bitmap = imageBitmap,
                    pdfBytes = data,
                    fileName = "transactions"
                )
            }
        } else {
            updateState { it.copy(isLoading = false) }
            showErrorSnackBar(Res.string.error_no_data)
        }
    }

    private suspend fun onGetBitmapError(throwable: Throwable) {
        updateState { it.copy(isLoading = false) }
        showErrorSnackBar(Res.string.something_went_wrong)
    }

    override fun onBackClicked() {
        sendEffect(ViewTransactionsEffect.NavigateBack)
    }

    override fun onShareClicked(pdfBytes: ByteArray, fileName: String) {
        tryToExecute(
            callee = {
                pdfShare.sharePdf(
                    pdfBytes = pdfBytes,
                    fileName = "$fileName.pdf",
                    mimeType = PDF_TYPE
                )
            },
            onSuccess = {
                updateState { it.copy(isShareLoading = false) }
                showSuccessSnackBar(
                    titleRes = Res.string.success,
                    messageRes = Res.string.file_shared_successfully
                )
            },
            onError = ::onShareError,
            onStart = ::onShareStart,
            dispatcher = ioDispatcher
        )
    }

    private fun onShareStart() {
        updateState { it.copy(isShareLoading = true) }
    }

    private suspend fun onShareError(throwable: Throwable) {
        updateState { it.copy(isShareLoading = false) }
        when (throwable) {
            is IllegalStateException -> {
                showErrorSnackBar(Res.string.error_no_data)
            }

            else -> {
                showErrorSnackBar(Res.string.something_went_wrong)
            }
        }
    }

    private suspend fun showSuccessSnackBar(
        titleRes: StringResource,
        messageRes: StringResource,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    titleRes = titleRes,
                    messageRes = messageRes,
                    isSuccess = true
                )
            )
        }
        delay(durationMillis)
        hideSnackBar()
    }

    private suspend fun showErrorSnackBar(
        messageRes: StringResource,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    titleRes = Res.string.error,
                    messageRes = messageRes,
                    isSuccess = false
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

    private companion object {
        const val PDF_TYPE = "application/pdf"
    }
}