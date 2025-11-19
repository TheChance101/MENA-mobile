package net.thechance.mena.wallet.presentation.screen.transaction_history

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
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.failed_to_apply_filters
import mena.wallet_presentation.generated.resources.start_date_must_be_before_end_date
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.Paginator
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionHistoryViewModel(
    @Provided private val transactionRepository: TransactionRepository,
    @Provided private val stringProvider: StringProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<TransactionHistoryScreenState, TransactionHistoryEffect>(
    TransactionHistoryScreenState()
), TransactionHistoryInteractionListener {

    private lateinit var paginator: Paginator<Int, List<Transaction>>

    init {
        initializePaginator()
        fetchFirstTransactionDate()
        loadNextTransactions()
    }

    private fun initializePaginator() {
        paginator = Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedTransactions,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = ::onPaginationError,
            onSuccess = { result, newKey -> onPaginationSuccess(result, newKey) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
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
        updateState { it.copy(filterState = TransactionFilterState(), isFilterVisible = false) }
        resetPaginator()
    }

    override fun onApplyFilterClicked() {
        if (areDatesValid().not()) {
            showInvalidDatesSnackBar()
            return
        }
        updateState { it.copy(filterState = it.filterState.copy(isApplyButtonLoading = true)) }
        resetPaginator()
    }

    override fun onStartDateClicked() {
        val currentStartDate = currentState.filterState.startDate
        if (currentStartDate != null) {
            updateState { it.copy(filterState = it.filterState.copy(defaultStartDate = currentStartDate)) }
        }
        updateState {
            it.copy(
                isFilterVisible = false,
                filterState = it.filterState.copy(
                    isDateBottomSheetVisible = true,
                    datePickerMode = TransactionFilterState.DatePickerMode.START_DATE,
                )
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onEndDateClicked() {
        val currentEndDate = currentState.filterState.endDate
        updateState {
            it.copy(
                isFilterVisible = false,
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
                isFilterVisible = true,
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

    override fun onFilterTypeSelected(type: FilterType) {
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


    override fun onFilterStatusSelected(status: FilterStatus) {
        updateState { it.copy(filterState = it.filterState.copy(selectedStatus = status)) }
    }


    override fun onDismissFilter() {
        updateState {
            it.copy(isFilterVisible = false)
        }
    }

    override fun onNextPageRequested() {
        loadNextTransactions()
    }

    override fun onRetryLoadTransactionHistoryClicked() {
        loadNextTransactions()
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
        updateState {
            val currentStartDate = it.filterState.startDate ?: date
            it.copy(
                filterState = it.filterState.copy(
                    defaultStartDate = currentStartDate,
                    errorState = null
                )
            )
        }
    }

    private fun onFetchFirstTransactionDateError(error: ErrorState) {
        updateState { it.copy(errorState = error) }
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

    private fun getActiveFilterCount(): Int {
        val state = currentState.filterState
        return (if (state.selectedTypes.isNotEmpty()) 1 else 0) +
                (if (state.selectedStatus != FilterStatus.ALL) 1 else 0) +
                (if (state.startDate != null || state.endDate != null) 1 else 0)
    }

    private fun resetPaginator() {
        paginator.reset()
        loadNextTransactions()
    }

    private fun areDatesValid(): Boolean {
        val startDate = currentState.filterState.startDate
        val endDate = currentState.filterState.endDate
        return (startDate != null && endDate != null && startDate > endDate).not()
    }

    private fun loadNextTransactions() {
        viewModelScope.launch(dispatcher) {
            paginator.loadNextItems()
        }
    }

    private fun onPaginationLoading(isLoading: Boolean) {
        updateState {
            it.copy(
                isLoading = isLoading && currentState.history.isEmpty(),
                isPaginationLoading = isLoading && currentState.history.isNotEmpty(),
            )
        }

        if (isLoading) {
            updateState { it.copy(errorState = null) }
        }
    }

    private suspend fun getPagedTransactions(page: Int): List<Transaction> =
        transactionRepository.getTransactionHistory(
            page = page,
            pageSize = PAGE_SIZE,
            transactionFilterParams = currentState.filterState.toParams()
        )

    private fun onPaginationSuccess(items: List<Transaction>, newKey: Int) {
        val newItems = items.map { transaction -> transaction.toUi() }

        updateState {
            it.copy(
                history = if (newKey == INITIAL_PAGE + 1) newItems else it.history + newItems,
                endOfPages = items.isEmpty()
            )
        }

        if (currentState.isFilterVisible) {
            updateState {
                it.copy(
                    isFilterVisible = false,
                    filterState = it.filterState.copy(
                        isApplyButtonLoading = false,
                        activeFilterCount = getActiveFilterCount(),
                        oldFilterHash = it.filterState.currentFilterHash
                    )
                )
            }
        }
    }

    private fun onPaginationError(throwable: Throwable?) {
        if (currentState.isFilterVisible) {
            showFilterErrorSnackBar()
        }
        updateState {
            it.copy(
                errorState = when (throwable) {
                    is NoInternetException -> ErrorState.NoInternet
                    else -> ErrorState.UnknownError
                },
                isLoading = false,
                filterState = it.filterState.copy(isApplyButtonLoading = false)
            )
        }
    }

    private fun showFilterErrorSnackBar() {
        viewModelScope.launch (Dispatchers.Main){
            showSnackBar(
                title = stringProvider.getString(Res.string.error),
                message = stringProvider.getString(Res.string.failed_to_apply_filters),
                isSuccess = false
            )
        }
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
        updateState { oldState -> oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false)) }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}