package net.thechance.mena.wallet.presentation.screen.export

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.download_complete
import mena.wallet_presentation.generated.resources.download_failed
import mena.wallet_presentation.generated.resources.download_success
import mena.wallet_presentation.generated.resources.downloading_started
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.error_failed_view
import mena.wallet_presentation.generated.resources.error_no_transactions
import mena.wallet_presentation.generated.resources.something_went_wrong
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.exceptions.NoDataFoundException
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.ExportTransactionsRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.CustomToastState
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.screen.export.file_saver.FileSaver
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.time.ExperimentalTime

@KoinViewModel
class ExportTransactionsViewModel(
    @Provided private val exportTransactionsRepository: ExportTransactionsRepository,
    @Provided private val fileSaver: FileSaver,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ExportTransactionsState, ExportTransactionsEffect>(
    ExportTransactionsState()
), ExportTransactionsListener {

    override fun onBackClicked() {
        sendEffect(ExportTransactionsEffect.NavigateBack)
    }

    override fun onAllTransactionsClicked() {
        updateState { oldState ->
            oldState.copy(
                isCustomFilterCardSelected = false,
                isDownloadButtonEnabled = true,
                isViewAndShareButtonEnabled = true
            )
        }
    }

    override fun onCustomFilteringClicked() {
        updateState { oldState ->
            oldState.copy(
                isCustomFilterCardSelected = true,
                isDownloadButtonEnabled = oldState.hasActiveFilters,
                isViewAndShareButtonEnabled = oldState.hasActiveFilters
            )
        }
    }

    override fun onTypeSelected(type: FilterType) {
        val current = currentState.selectedTransactionsTypes
        val newSet = if (current.contains(type)) current - type else current + type

        val newState = currentState.copy(selectedTransactionsTypes = newSet)

        updateState {
            newState.copy(
                isDownloadButtonEnabled =
                    if (newState.isCustomFilterCardSelected) {
                        newState.hasActiveFilters
                    } else {
                        true
                    },
                isViewAndShareButtonEnabled =
                    if (newState.isCustomFilterCardSelected) {
                        newState.hasActiveFilters
                    } else {
                        true
                    },
                hasNoTransactionsError = false
            )
        }
    }

    override fun onFromDateClicked() {
        //TODO Here the DatePicker opens and stores the result in state.startDate
        updateState { oldState -> oldState.copy(startDate = "2025/09/01") }
    }

    override fun onToDateClicked() {
        //TODO Here the DatePicker opens and stores the result in state.endDate
        updateState { oldState -> oldState.copy(endDate = "2025/09/27") }
    }

    override fun onViewAndShareClicked() {
        tryToExecute(
            onStart = ::onViewAndShareStart,
            callee = ::generateTransactionsFile,
            onSuccess = { pdfBytes -> onViewAndShareSuccess(pdfBytes) },
            onError = { error -> onViewAndShareError(error) },
            dispatcher = ioDispatcher
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun onDownloadClicked() {
        tryToExecute(
            onStart = ::onDownloadStart,
            callee = ::generateTransactionsFile,
            onSuccess = { pdfBytes -> saveFile(pdfBytes) },
            onError = { error -> handleDownloadError(error) },
            dispatcher = ioDispatcher
        )
    }

    private suspend fun onViewAndShareStart() {
        if (currentState.hasNoTransactionsError) {
            showToast(messageRes = Res.string.error_no_transactions)
            return
        }
        updateState { oldState ->
            oldState.copy(
                isViewAndShareLoading = true,
                isDownloadButtonEnabled = false
            )
        }
    }

    private suspend fun onViewAndShareSuccess(pdfBytes: ByteArray) {
        resetViewAndShareState()
        sendEffect(ExportTransactionsEffect.NavigateToViewFileScreen)
    }

    private suspend fun onViewAndShareError(error: Throwable) {
        resetViewAndShareState()
        handleError(
            error = error,
            titleRes = Res.string.error,
            messageRes = Res.string.error_failed_view,
            isSuccess = false
        )
    }

    private suspend fun onDownloadStart() {
        if (currentState.hasNoTransactionsError) {
            showToast(messageRes = Res.string.error_no_transactions)
            return
        }

        updateState { oldState ->
            oldState.copy(
                isDownloadLoading = true,
                isViewAndShareButtonEnabled = false
            )
        }
        showToast(messageRes = Res.string.downloading_started)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun generateTransactionsFile(): ByteArray {
        return if (currentState.isCustomFilterCardSelected) {
            val formatter = LocalDate.Format {
                year(); char('-'); monthNumber(); char('-');
                day(padding = Padding.ZERO)
            }
            val startDateTime: LocalDate? =
                currentState.startDate.toStartOfDayLocalDateTime(formatter)

            val endDateTime: LocalDate? = currentState.endDate
                .toStartOfDayLocalDateTime(formatter)

            exportTransactionsRepository.getFilteredTransactionsFile(
                TransactionFilterParams(
                    types = currentState.selectedTransactionsTypes?.map { it.toDomain() },
                    startDate = startDateTime,
                    endDate = endDateTime
                )
            )
        } else {
            exportTransactionsRepository.getFilteredTransactionsFile()
        }
    }

    private suspend fun handleDownloadError(error: Throwable) {
        resetDownloadState()
        handleError(
            error = error,
            titleRes = Res.string.download_failed,
            messageRes = Res.string.something_went_wrong,
            isSuccess = false
        )
    }

    private suspend fun saveFile(pdfBytes: ByteArray) {
        try {
            val isFileSaved = fileSaver.saveFile(
                suggestedName = "transactions",
                extension = "pdf",
                bytes = pdfBytes
            )
            resetDownloadState()
            if (isFileSaved) {
                showSnackBar(
                    titleRes = Res.string.download_complete,
                    messageRes = Res.string.download_success,
                    isSuccess = true
                )
            } else {
                showSnackBar(
                    titleRes = Res.string.download_failed,
                    messageRes = Res.string.something_went_wrong,
                    isSuccess = false
                )
            }
        } catch (error: Exception) {
            resetDownloadState()
            handleError(
                error = error,
                titleRes = Res.string.download_failed,
                messageRes = Res.string.something_went_wrong,
                isSuccess = false
            )
        }
    }

    private suspend fun handleError(
        error: Throwable,
        titleRes: StringResource,
        messageRes: StringResource,
        isSuccess: Boolean = false
    ) {
        when (error) {
            is NoInternetException -> {
                updateState { oldState ->
                    oldState.copy(
                        noInternetConnection = true,
                        isDownloadLoading = false,
                        isViewAndShareLoading = false
                    )
                }
                showSnackBar(
                    titleRes = Res.string.download_failed,
                    messageRes = Res.string.something_went_wrong,
                    isSuccess = false
                )
            }

            is NoDataFoundException -> {
                updateState { oldState ->
                    oldState.copy(
                        isDownloadLoading = false,
                        isViewAndShareLoading = false,
                        hasNoTransactionsError = true
                    )
                }
                showToast(messageRes = Res.string.error_no_transactions)
            }

            else -> {
                showSnackBar(
                    titleRes = titleRes,
                    messageRes = messageRes,
                    isSuccess = isSuccess
                )
            }
        }
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
            oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false))
        }
    }

    private suspend fun showToast(
        messageRes: StringResource,
        durationMillis: Long = 2000L
    ) {
        updateState { oldState ->
            oldState.copy(
                toast = CustomToastState(
                    isVisible = true,
                    messageRes = messageRes
                )
            )
        }
        delay(durationMillis)
        hideToast()
    }

    private fun hideToast() {
        updateState { oldState ->
            oldState.copy(
                toast = oldState.toast.copy(
                    isVisible = false
                )
            )
        }
    }

    private fun resetDownloadState() {
        updateState { oldState ->
            oldState.copy(
                isDownloadLoading = false,
                isViewAndShareButtonEnabled = true
            )
        }
    }

    private fun resetViewAndShareState() {
        updateState { oldState ->
            oldState.copy(
                isViewAndShareLoading = false,
                isDownloadButtonEnabled = true
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun String?.toStartOfDayLocalDateTime(formatter: DateTimeFormat<LocalDate>):
            LocalDate? {
        return this
            ?.takeIf { it.isNotEmpty() }
            ?.let { LocalDate.parse(it, formatter) }
    }
}