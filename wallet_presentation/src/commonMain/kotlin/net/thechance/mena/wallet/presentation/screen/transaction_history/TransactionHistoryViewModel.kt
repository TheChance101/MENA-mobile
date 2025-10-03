package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.error
import mena.wallet_presentation.generated.resources.failed_to_apply_filters
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
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

    init {
        getTransactionHistory()
    }

    private fun getTransactionHistory(filters: TransactionFilterParams? = null) {
        tryToExecute(
            callee = { transactionRepository.getTransactionHistory(filters) },
            onStart = ::onGetTransactionHistoryStart,
            onSuccess = ::onGetTransactionHistorySuccess,
            onError = ::onGetTransactionHistoryError,
            dispatcher = Dispatchers.IO
        )
    }

    private fun onGetTransactionHistoryStart() {
        updateState {
            it.copy(
                isLoading = true,
                isError = null
            )
        }
    }

    private fun onGetTransactionHistorySuccess(transactionHistory: List<Transaction>) {
        updateState {
            it.copy(
                history = transactionHistory.map { it -> it.toUi() },
                isLoading = false,
                isError = null
            )
        }
    }

    private fun onGetTransactionHistoryError(throwable: Throwable) {
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
}