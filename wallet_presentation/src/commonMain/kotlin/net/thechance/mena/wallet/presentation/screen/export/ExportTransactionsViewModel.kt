package net.thechance.mena.wallet.presentation.screen.export

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.download_complete
import mena.wallet_presentation.generated.resources.download_failed
import mena.wallet_presentation.generated.resources.download_success
import mena.wallet_presentation.generated.resources.downloading_started
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.error_failed_view
import mena.wallet_presentation.generated.resources.error_no_transactions
import mena.wallet_presentation.generated.resources.failed_to_load_date_picker
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.something_went_wrong
import mena.wallet_presentation.generated.resources.start_date_must_be_before_end_date
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.CustomToastState
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.screen.export.file_saver.FileSaver
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@KoinViewModel
class ExportTransactionsViewModel(
    @Provided private val transactionRepository: TransactionRepository,
    @Provided private val statementRepository: StatementRepository,
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
                    if (newState.isCustomFilterCardSelected) newState.hasActiveFilters else true,
                isViewAndShareButtonEnabled =
                    if (newState.isCustomFilterCardSelected) newState.hasActiveFilters else true,
                hasNoTransactionsError = false
            )
        }
    }

    override fun onStartDateClicked() {
        val currentStartDate = currentState.startDate
        if (currentStartDate != null) {
            openStartDatePickerWithExistingDate(currentStartDate)
        } else {
            fetchFirstTransactionDate()
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onEndDateClicked() {
        val currentEndDate = currentState.endDate
        updateState {
            it.copy(
                isDateBottomSheetVisible = true,
                datePickerMode = ExportTransactionsState.DatePickerMode.END_DATE,
                defaultEndDate = currentEndDate ?: Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        }
    }

    override fun onDismissDatePicker() {
        updateState {
            it.copy(
                isDateBottomSheetVisible = false
            )
        }
    }

    override fun onPickDateClicked(date: LocalDate) {
        val updatedState = when (currentState.datePickerMode) {
            ExportTransactionsState.DatePickerMode.START_DATE -> updateStartDate(date)
            ExportTransactionsState.DatePickerMode.END_DATE -> updateEndDate(date)
        }

        applyStateWithUpdatedButtons(updatedState)
        onDismissDatePicker()
    }


    override fun onViewAndShareClicked() {
        if (areDatesValid().not()) {
            showInvalidDatesSnackBar()
            return
        }
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
        if (areDatesValid().not()) {
            showInvalidDatesSnackBar()
            return
        }
        tryToExecute(
            onStart = ::onDownloadStart,
            callee = ::generateTransactionsFile,
            onSuccess = { pdfBytes -> saveFile(pdfBytes) },
            onError = { error -> handleDownloadError(error) },
            dispatcher = ioDispatcher
        )
    }

    private fun areDatesValid(): Boolean {
        val startDate = currentState.startDate
        val endDate = currentState.endDate
        return (startDate != null && endDate != null && startDate > endDate).not()
    }

    private fun showInvalidDatesSnackBar() {
        viewModelScope.launch {
            showSnackBar(
                titleRes = Res.string.error,
                messageRes = Res.string.start_date_must_be_before_end_date,
                isSuccess = false
            )
        }
    }

    private fun updateStartDate(date: LocalDate): ExportTransactionsState {
        return currentState.copy(
            startDate = date,
            defaultStartDate = date
        )
    }

    private fun updateEndDate(date: LocalDate): ExportTransactionsState {
        return currentState.copy(
            endDate = date,
            defaultEndDate = date
        )
    }

    private fun applyStateWithUpdatedButtons(newState: ExportTransactionsState) {
        updateState {
            newState.copy(
                isDownloadButtonEnabled =
                    if (newState.isCustomFilterCardSelected) newState.hasActiveFilters else true,
                isViewAndShareButtonEnabled =
                    if (newState.isCustomFilterCardSelected) newState.hasActiveFilters else true
            )
        }
    }

    private fun openStartDatePickerWithExistingDate(currentStartDate: LocalDate) {
        updateState {
            it.copy(
                isDateBottomSheetVisible = true,
                datePickerMode = ExportTransactionsState.DatePickerMode.START_DATE,
                defaultStartDate = currentStartDate
            )
        }
    }

    private fun fetchFirstTransactionDate() {
        tryToExecute(
            callee = { transactionRepository.getFirstTransactionDate() },
            onSuccess = ::onGetFirstTransactionDateSuccess,
            onError = ::onGetFirstTransactionDateError,
            dispatcher = Dispatchers.IO
        )
    }

    private suspend fun onGetFirstTransactionDateError(throwable: ErrorState) {
        handleError(
            error = throwable,
            titleRes = Res.string.error,
            messageRes = Res.string.failed_to_load_date_picker,
            isSuccess = false
        )
    }

    private fun onGetFirstTransactionDateSuccess(date: LocalDate?) {
        updateState {
            val currentStartDate = it.startDate ?: date
            it.copy(
                defaultStartDate = currentStartDate,
                isDateBottomSheetVisible = true,
                datePickerMode = ExportTransactionsState.DatePickerMode.START_DATE,
            )
        }
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

    private fun onViewAndShareSuccess(pdfBytes: ByteArray) {
        resetViewAndShareState()
        sendEffect(ExportTransactionsEffect.NavigateToViewFileScreen(getTransactionFilterParams()))
    }

    private suspend fun onViewAndShareError(error: ErrorState) {
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
            oldState.copy(isDownloadLoading = true, isViewAndShareButtonEnabled = false)
        }
        showToast(messageRes = Res.string.downloading_started)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun generateTransactionsFile(): ByteArray {
        return if (currentState.isCustomFilterCardSelected) {

            statementRepository.getTransactionsPdf(
                getTransactionFilterParams()
            )
        } else {
            statementRepository.getTransactionsPdf()
        }
    }

    private fun getTransactionFilterParams(): TransactionFilterParams {
        val formatter = LocalDate.Format {
            year(); char('-'); monthNumber(); char('-');
            day(padding = Padding.ZERO)
        }
        val startDateTime = currentState.startDate?.toString().toStartOfDayLocalDateTime(formatter)

        val endDateTime = currentState.endDate?.toString().toStartOfDayLocalDateTime(formatter)

        return TransactionFilterParams(
            types = currentState.selectedTransactionsTypes.map { it.toDomain() },
            startDate = startDateTime,
            endDate = endDateTime
        )
    }

    private suspend fun handleDownloadError(error: ErrorState) {
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
        } catch (_: Exception) {
            resetDownloadState()
            handleError(
                error = ErrorState.Unknown,
                titleRes = Res.string.download_failed,
                messageRes = Res.string.something_went_wrong,
                isSuccess = false
            )
        }
    }

    private suspend fun handleError(
        error: ErrorState,
        titleRes: StringResource,
        messageRes: StringResource,
        isSuccess: Boolean = false
    ) {
        when (error) {
            is ErrorState.NoInternet -> {
                updateState { oldState ->
                    oldState.copy(
                        noInternetConnection = true,
                        isDownloadLoading = false,
                        isViewAndShareLoading = false
                    )
                }
                showSnackBar(
                    titleRes = Res.string.download_failed,
                    messageRes = Res.string.no_internet_title,
                    isSuccess = false
                )
            }

            is ErrorState.NoDataFound -> {
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
            oldState.copy(isDownloadLoading = false, isViewAndShareButtonEnabled = true)
        }
    }

    private fun resetViewAndShareState() {
        updateState { oldState ->
            oldState.copy(isViewAndShareLoading = false, isDownloadButtonEnabled = true)
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