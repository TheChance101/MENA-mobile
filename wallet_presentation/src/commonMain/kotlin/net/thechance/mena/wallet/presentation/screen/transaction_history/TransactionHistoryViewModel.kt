package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.failed_to_apply_filters
import mena.wallet_presentation.generated.resources.failed_to_load_date_picker
import mena.wallet_presentation.generated.resources.start_date_must_be_before_end_date
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.Paginator
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionHistoryViewModel(
    @Provided private val transactionRepository: TransactionRepository
) : BaseViewModel<TransactionHistoryScreenState, TransactionHistoryEffect>(
    TransactionHistoryScreenState()
), TransactionHistoryInteractionListener {

    init {
        loadNextTransactions()
    }

    override fun onBackClicked() {
        sendEffect(TransactionHistoryEffect.NavigateBack)
    }

    override fun onTransactionCardClicked(id: Uuid) {
        sendEffect(TransactionHistoryEffect.NavigateToTransactionDetails(id))
    }

    override fun onExportClicked() {
        sendEffect(TransactionHistoryEffect.NavigateToExportTransaction)
    }

    override fun onFilterClicked() {
        updateState { it.copy(isFilterVisible = true) }
    }

    override fun onResetFilterClicked() {
        updateState { it.copy(filterState = TransactionFilterState()) }
        /*
        TODO: Invalidate paginator and re-fetch data with the new filter.
         */
    }

    override fun onApplyFilterClicked() {
        val filters = state.value.filterState
        if (areDatesValid().not()) {
            showInvalidDatesSnackBar()
            return
        }
        tryToExecute(
            callee = {
                transactionRepository.getTransactionHistory(
                    page = 1,
                    pageSize = PAGE_SIZE,
                    transactionFilterParams = filters.toParams()
                )
            },
            onStart = ::onGetTransactionFilterStart,
            onSuccess = ::onGetTransactionFilterSuccess,
            onError = ::onGetTransactionFilterError,
            dispatcher = Dispatchers.IO
        )
    }


    private fun areDatesValid(): Boolean {
        val startDate = currentState.filterState.startDate
        val endDate = currentState.filterState.endDate
        return (startDate != null && endDate != null && startDate > endDate).not()
    }

    override fun onStartDateClicked() {
        val currentStartDate = currentState.filterState.startDate
        if (currentStartDate != null) {
            openStartDatePickerWithExistingDate(currentStartDate)
        } else {
            fetchFirstTransactionDate()
        }
    }



    @OptIn(ExperimentalTime::class)
    override fun onEndDateClicked() {
        val currentEndDate = currentState.filterState.endDate
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isDateBottomSheetVisible = true,
                    datePickerMode = TransactionFilterState.DatePickerMode.END_DATE,
                    defaultEndDate = currentEndDate ?: Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                )
            )
        }
    }

    override fun onDismissDatePicker() {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isDateBottomSheetVisible = false
                )
            )
        }
    }

    override fun onPickDateClicked(date: LocalDate) {
        when (currentState.filterState.datePickerMode) {
            TransactionFilterState.DatePickerMode.START_DATE -> updateStartDate(date)
            TransactionFilterState.DatePickerMode.END_DATE -> updateEndDate(date)
        }
        onDismissDatePicker()
    }

    override fun selectFilterType(type: FilterType) {
        updateState {
            val currentTypes = it.filterState.selectedTypes.toMutableSet()
            if (currentTypes.contains(type)) {
                currentTypes.remove(type)
            } else {
                currentTypes.add(type)
            }
            it.copy(filterState = it.filterState.copy(selectedTypes = currentTypes))
        }
    }


    override fun selectFilterStatus(status: FilterStatus) {
        updateState { it.copy(filterState = it.filterState.copy(selectedStatus = status)) }
    }


    override fun onDismissFilter() {
        updateState {
            it.copy(isFilterVisible = false)
        }
    }

    private fun openStartDatePickerWithExistingDate(currentStartDate: LocalDate) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isDateBottomSheetVisible = true,
                    datePickerMode = TransactionFilterState.DatePickerMode.START_DATE,
                    defaultStartDate = currentStartDate
                )
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
        updateState {
            it.copy(
                filterState = it.filterState.copy(errorState = throwable)
            )
        }

        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.failed_to_load_date_picker,
            isSuccess = false
        )
    }

    private fun onGetFirstTransactionDateSuccess(date: LocalDate?) {
        updateState {
            val currentStartDate = it.filterState.startDate ?: date
            it.copy(
                filterState = it.filterState.copy(
                    defaultStartDate = currentStartDate,
                    isDateBottomSheetVisible = true,
                    datePickerMode = TransactionFilterState.DatePickerMode.START_DATE,
                    errorState = null
                )
            )
        }
    }

    private fun updateStartDate(date: LocalDate) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    startDate = date,
                    defaultStartDate = date
                )
            )
        }
    }

    private fun updateEndDate(date: LocalDate) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    endDate = date,
                    defaultEndDate = date
                )
            )
        }
    }

    private fun onGetTransactionFilterStart() {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isLoading = true,
                    errorState = null
                )
            )
        }
    }

    private fun onGetTransactionFilterSuccess(transactionHistory: List<Transaction>) {
        updateState {
            it.copy(
                isFilterVisible = false,
                history = transactionHistory.map { tx -> tx.toUi() },
                filterState = it.filterState.copy(
                    isLoading = false,
                    activeFilterCount = getActiveFilterCount()
                )
            )
        }
    }


    private fun getActiveFilterCount(): Int {
        val state = currentState.filterState
        return (if (state.selectedTypes.isNotEmpty()) 1 else 0) +
                (if (state.selectedStatus != FilterStatus.ALL) 1 else 0) +
                (if (state.startDate != null || state.endDate != null) 1 else 0)
    }

    override fun onNextPageRequested() {
        loadNextTransactions()
    }

    override fun onRetryLoadTransactionHistoryClicked() {
        loadNextTransactions()
    }

    private suspend fun onGetTransactionFilterError(errorState: ErrorState) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isLoading = false,
                    errorState = errorState
                )
            )
        }

        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.failed_to_apply_filters,
            isSuccess = false
        )
    }

    private fun loadNextTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }

    private fun onPaginationLoading(isLoading: Boolean) {
        updateState {
            if (isLoading) {
                if (it.history.isEmpty()) {
                    it.copy(isLoading = true, errorState = null)
                } else {
                    it.copy(isPaginationLoading = true, errorState = null)
                }
            } else {
                it.copy(isLoading = false, isPaginationLoading = false)
            }
        }
    }

    private suspend fun getPagedTransactions(
        page: Int,
        pageSize: Int = PAGE_SIZE
    ): List<Transaction> = transactionRepository.getTransactionHistory(
        page = page,
        pageSize = pageSize,
        transactionFilterParams = currentState.filterState.toParams()
    )

    private fun onPaginationSuccess(items: List<Transaction>) {
        updateState {
            it.copy(
                history = it.history + items.map { transaction -> transaction.toUi() },
                endOfPages = items.isEmpty()
            )
        }
    }

    private fun onPaginationError(throwable: Throwable) {
        updateState { it.copy(errorState = ErrorState.Unknown) }
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
        updateState { oldState -> oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false)) }
    }

    private val paginator by lazy {
        Paginator(
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedTransactions,
            onSuccess = ::onPaginationSuccess,
            onError = ::onPaginationError,
            pageSize = PAGE_SIZE
        )
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}