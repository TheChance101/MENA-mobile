package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.failed_to_apply_filters
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.utils.Paginator
import net.thechance.mena.wallet.presentation.base.SnackBarState
import net.thechance.mena.wallet.presentation.model.FilterStatus
import net.thechance.mena.wallet.presentation.model.FilterType
import org.jetbrains.compose.resources.StringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@KoinViewModel
class TransactionHistoryViewModel(
    @Provided private val transactionRepository: TransactionRepository
) : BaseViewModel<TransactionHistoryScreenState, TransactionHistoryEffect>(
    TransactionHistoryScreenState()
), TransactionHistoryInteractionListener {
    companion object {
        const val PAGE_SIZE = 20
    }

    private val paginator =
        Paginator(
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedTransactions,
            onSuccess = ::onPaginationSuccess,
            onError = ::onPaginationError,
            pageSize = PAGE_SIZE
        )

    init {
        loadNextTransactions()
    }

    fun loadNextTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            paginator.loadNextItems()
        }
    }
    private fun onPaginationLoading(isLoading: Boolean) {
        updateState {
            if (isLoading) {
                if (it.history.isEmpty()) {
                    it.copy(isLoading = true, isError = null)
                } else {
                    it.copy(isPaginationLoading = true, isError = null)
                }
            } else {
                if (it.history.isEmpty()) {
                    it.copy(isLoading = false)
                } else {
                    it.copy(isPaginationLoading = false)
                }
            }
        }
    }

    private suspend fun getPagedTransactions(
        page: Int,
        pageSize: Int = PAGE_SIZE
    ): List<Transaction> = transactionRepository.getTransactionHistory(
        page = page,
        pageSize = pageSize,
        TransactionFilterParams()
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
        updateState { it.copy(isError = throwable) }
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
        updateState {
            it.copy(isFilterVisible = true)
        }
    }

    override fun onResetFilterClicked() {
        updateState {
            it.copy(
                filterState = TransactionFilterState()
            )
        }
        getTransactionHistory(filters = null)
    }

    override fun onApplyFilterClicked() {

        val filters = state.value.filterState
        tryToExecute(
            callee = {
                transactionRepository.getTransactionHistory(
                    filters.toParams()
                )
            },
            onStart = ::onGetTransactionFilterStart,
            onSuccess = { it ->
                onGetTransactionFilterSuccess(it)
                updateState {
                    it.copy(isFilterVisible = false)
                }
            },
            onError = ::onGetTransactionFilterError,
            dispatcher = Dispatchers.IO
        )
    }

    private fun onGetTransactionFilterStart() {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isLoading = true,
                    isError = null
                )
            )
        }
    }

    private fun onGetTransactionFilterSuccess(transactionHistory: List<Transaction>) {
        updateState {
            it.copy(
                history = transactionHistory.map { tx -> tx.toUi() },
                filterState = it.filterState.copy(
                    isLoading = false,
                    activeFilterCount = getActiveFilterCount()
                )
            )
        }
    }

    private fun getActiveFilterCount(): Int {
        val state = state.value.filterState
        return (if(state.selectedTypes.isNotEmpty()) 1 else 0) +
                (if (state.selectedStatus != FilterStatus.ALL) 1 else 0) +
                (if (state.fromDate != null || state.toDate != null) 1 else 0)
    }

    private suspend fun onGetTransactionFilterError(throwable: Throwable) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    isLoading = false,
                    isError = throwable
                )
            )
        }

        showSnackBar(
            titleRes = Res.string.error,
            messageRes = Res.string.failed_to_apply_filters,
            isSuccess = false
        )
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

    override fun selectFilterType(type: FilterType) {
        updateState {
            val currentTypes = it.filterState.selectedTypes.toMutableSet()
            if (currentTypes.contains(type)) {
                currentTypes.remove(type)
            } else {
                currentTypes.add(type)
            }
            it.copy(
                filterState = it.filterState.copy(
                    selectedTypes = currentTypes
                )
            )
        }
    }

    override fun selectFilterStatus(status: FilterStatus) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    selectedStatus = status
                )
            )
        }
    }

    fun updateFromDate(date: LocalDate?) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    fromDate = date
                )
            )
        }
    }

    fun updateToDate(date: LocalDate?) {
        updateState {
            it.copy(
                filterState = it.filterState.copy(
                    toDate = date
                )
            )
        }
    }

    override fun onDismissFilter() {
        updateState {
            it.copy(
                isFilterVisible = false
            )
        }
    }

    override fun onNextPageRequested() {
        loadNextTransactions()
    }

    override fun onRetry() {
        loadNextTransactions()
    }
}