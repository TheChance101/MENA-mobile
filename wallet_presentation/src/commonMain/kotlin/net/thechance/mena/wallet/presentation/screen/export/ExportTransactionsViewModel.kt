package net.thechance.mena.wallet.presentation.screen.export

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.download_complete
import mena.wallet_presentation.generated.resources.download_failed
import mena.wallet_presentation.generated.resources.download_success
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.error_failed_view
import mena.wallet_presentation.generated.resources.error_no_transactions
import mena.wallet_presentation.generated.resources.failed_to_load_date_picker
import mena.wallet_presentation.generated.resources.no_internet_content
import mena.wallet_presentation.generated.resources.no_internet_title
import mena.wallet_presentation.generated.resources.something_went_wrong
import mena.wallet_presentation.generated.resources.start_date_must_be_before_end_date
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.model.StatementWithMetaData
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.FileManager
import net.thechance.mena.wallet.presentation.utils.MimeType
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class ExportTransactionsViewModel(
    @Provided private val transactionRepository: TransactionRepository,
    @Provided private val statementRepository: StatementRepository,
    @Provided private val fileManager: FileManager,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ExportTransactionsState, ExportTransactionsEffect>(
    ExportTransactionsState()
), ExportTransactionsListener {

    init {
        fetchFirstTransactionDate()
    }

    override fun onBackClicked() {
        sendEffect(ExportTransactionsEffect.NavigateBack)
    }

    override fun onAllTransactionsClicked() {
        updateState { oldState ->
            oldState.copy(
                isCustomFilterCardSelected = false,
                isDownloadButtonEnabled = true,
                isViewAndShareButtonEnabled = true,
                filterState = ExportTransactionsState.FilterState()
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
        val current = currentState.filterState.selectedTransactionsTypes
        val newSet = if (current.contains(type)) current - type else current + type
        val newState =
            currentState.copy(filterState = currentState.filterState.copy(selectedTransactionsTypes = newSet))

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
        val currentStartDate = currentState.filterState.startDate
        if (currentStartDate != null) {
            updateState { it.copy(dateState = it.dateState.copy(defaultStartDate = currentStartDate)) }
        }
        updateState {
            it.copy(
                dateState = it.dateState.copy(
                    isDateBottomSheetVisible = true,
                    datePickerMode = ExportTransactionsState.DatePickerMode.START_DATE,
                )
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onEndDateClicked() {
        val currentEndDate = currentState.filterState.endDate
        updateState { oldState ->
            oldState.copy(
                dateState = oldState.dateState.copy(
                    isDateBottomSheetVisible = true,
                    datePickerMode = ExportTransactionsState.DatePickerMode.END_DATE,
                    defaultEndDate = currentEndDate ?: Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                )
            )
        }
    }

    override fun onDismissDatePicker() {
        updateState { it.copy(dateState = it.dateState.copy(isDateBottomSheetVisible = false)) }
    }

    override fun onPickDateClicked(date: LocalDate) {
        val updatedState = when (currentState.dateState.datePickerMode) {
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
            callee = ::getStatement,
            onSuccess = ::saveStatementToCache,
            onError = ::onViewAndShareError,
            dispatcher = dispatcher
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
            callee = ::getStatement,
            onSuccess = { statement -> downloadStatement(statement) },
            onError = ::handleDownloadError,
            dispatcher = dispatcher
        )
    }

    private fun areDatesValid(): Boolean {
        val startDate = currentState.filterState.startDate
        val endDate = currentState.filterState.endDate
        return (startDate != null && endDate != null && startDate > endDate).not()
    }

    private fun showInvalidDatesSnackBar() {
        viewModelScope.launch {
            showSnackBar(
                title = stringProvider.getString(Res.string.error),
                message = stringProvider.getString(Res.string.start_date_must_be_before_end_date),
                isSuccess = false
            )
        }
    }

    private fun updateStartDate(date: LocalDate): ExportTransactionsState {
        return currentState.copy(
            filterState = currentState.filterState.copy(startDate = date),
            dateState = currentState.dateState.copy(defaultStartDate = date)
        )
    }

    private fun updateEndDate(date: LocalDate): ExportTransactionsState {
        return currentState.copy(
            filterState = currentState.filterState.copy(endDate = date),
            dateState = currentState.dateState.copy(defaultEndDate = date)
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

    private fun fetchFirstTransactionDate() {
        tryToExecute(
            callee = { transactionRepository.getFirstTransactionDate() },
            onSuccess = ::onFetchFirstTransactionDateSuccess,
            onError = ::onFetchFirstTransactionDateError,
            dispatcher = dispatcher
        )
    }

    private fun onFetchFirstTransactionDateSuccess(date: LocalDate?) {
        updateState { oldState ->
            val currentStartDate = oldState.filterState.startDate ?: date
            oldState.copy(dateState = oldState.dateState.copy(defaultStartDate = currentStartDate))
        }
    }

    private suspend fun onFetchFirstTransactionDateError(error: ErrorState) {
        handleError(
            error = error,
            title = stringProvider.getString(Res.string.error),
            message = stringProvider.getString(Res.string.failed_to_load_date_picker),
            isSuccess = false
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

    private fun saveStatementToCache(statement: StatementWithMetaData) {
        tryToExecute(
            callee = { saveStatementPdfToCache(statement) },
            onSuccess = ::onSaveStatementToCacheSuccess,
            onError = ::onViewAndShareError
        )
    }

    private suspend fun saveStatementPdfToCache(statement: StatementWithMetaData): String {
        return fileManager.saveFile(
            data = statement.byteArray,
            location = StorageLocation.Cache(getUniqueStatementFileName()),
            mimeType = MimeType.PDF
        )
    }

    private fun onSaveStatementToCacheSuccess(statementPath: String) {
        resetViewAndShareState()

        val fileName = statementPath.substringAfterLast("/")
        sendEffect(
            ExportTransactionsEffect.NavigateToViewFileScreen(
                StorageLocation.Cache(fileName)
            )
        )
    }

    private suspend fun onViewAndShareError(error: ErrorState) {
        resetViewAndShareState()

        handleError(
            error = error,
            title = stringProvider.getString(Res.string.error),
            message = stringProvider.getString(Res.string.error_failed_view),
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
                isViewAndShareButtonEnabled = false,
                canSelectExportType = true
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun getStatement(): StatementWithMetaData {
        return if (currentState.isCustomFilterCardSelected) {
            statementRepository.getStatementWithMetadata(
                getTransactionFilterParams()
            )
        } else {
            statementRepository.getStatementWithMetadata()
        }
    }

    private fun getTransactionFilterParams(): TransactionFilterParams =
        TransactionFilterParams(
            types = currentState.filterState.selectedTransactionsTypes.map { it.toDomain() },
            startDate = currentState.filterState.startDate,
            endDate = currentState.filterState.endDate
        )

    private suspend fun handleDownloadError(error: ErrorState) {
        resetDownloadState()

        handleError(
            error = error,
            title = stringProvider.getString(Res.string.download_failed),
            message = stringProvider.getString(Res.string.something_went_wrong),
            isSuccess = false
        )
    }

    private fun downloadStatement(statement: StatementWithMetaData) {
        tryToExecute(
            callee = { saveStatementPdfToDownloads(statement) },
            onSuccess = { filePath -> onDownloadSuccess(filePath, statement) },
            onError = ::onDownloadFailure,
            dispatcher = dispatcher
        )
    }

    private suspend fun saveStatementPdfToDownloads(statement: StatementWithMetaData): String {
        return fileManager.saveFile(
            data = statement.byteArray,
            location = StorageLocation.Downloads(getUniqueStatementFileName()),
            mimeType = MimeType.PDF
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun onDownloadSuccess(filePath: String, statement: StatementWithMetaData) {
        resetDownloadState()

        val fileName = filePath.substringAfterLast("/")
        saveStatementToDatabase(
            filePath = filePath,
            statement = Statement(
                id = Uuid.random(),
                startDate = statement.startDate,
                endDate = statement.endDate,
                totalInflows = statement.totalInflows,
                totalOutflows = statement.totalOutflows,
                fileName = fileName
            )
        )
    }

    private fun saveStatementToDatabase(filePath: String, statement: Statement) {
        tryToExecute(
            callee = { statementRepository.insertStatement(statement) },
            onSuccess = { onSaveStatementSuccess(filePath) },
            onError = ::onDownloadFailure
        )
    }

    private suspend fun onSaveStatementSuccess(filePath: String) {
        showSnackBar(
            title = stringProvider.getString(Res.string.download_complete),
            message = stringProvider.getString(Res.string.download_success, filePath),
            isSuccess = true
        )
    }

    private suspend fun onDownloadFailure(error: ErrorState) {
        resetDownloadState()

        showSnackBar(
            title = stringProvider.getString(Res.string.download_failed),
            message = stringProvider.getString(Res.string.something_went_wrong),
            isSuccess = false
        )
    }

    private suspend fun handleError(
        error: ErrorState,
        title: String,
        message: String,
        isSuccess: Boolean = false
    ) {
        when (error) {
            is ErrorState.NoInternet -> {
                updateState { oldState ->
                    oldState.copy(
                        isDownloadLoading = false,
                        isViewAndShareLoading = false
                    )
                }
                showSnackBar(
                    title = stringProvider.getString(Res.string.no_internet_title),
                    message = stringProvider.getString(Res.string.no_internet_content),
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
                    title = title,
                    message = message,
                    isSuccess = isSuccess
                )
            }
        }
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
            oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false))
        }
    }

    private suspend fun showToast(
        messageRes: StringResource,
        durationMillis: Long = 2000L
    ) {
        updateState { oldState ->
            oldState.copy(
                toast = ExportTransactionsState.ToastState(
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
                ),
                hasNoTransactionsError = false
            )
        }
    }

    private fun resetDownloadState() {
        updateState { oldState ->
            oldState.copy(
                isDownloadLoading = false,
                isViewAndShareButtonEnabled = true,
                canSelectExportType = false
            )
        }
    }

    private fun resetViewAndShareState() {
        updateState { oldState ->
            oldState.copy(isViewAndShareLoading = false, isDownloadButtonEnabled = true)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getUniqueStatementFileName(): String {
        return "statement_${Clock.System.now().epochSeconds}.pdf"
    }
}
