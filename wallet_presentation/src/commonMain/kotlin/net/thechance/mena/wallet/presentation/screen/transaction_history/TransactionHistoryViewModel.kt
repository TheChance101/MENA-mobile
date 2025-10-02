package net.thechance.mena.wallet.presentation.screen.transaction_history

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.model.TransactionFilterParams
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.utils.Paginator
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
),
    TransactionHistoryInteractionListener {
    private val paginator =
        Paginator(
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedTransactions,
            onSuccess = ::onPaginationSuccess,
            onError = ::onPaginationError
        )

    init {
        loadNextTransactions()
    }

    fun loadNextTransactions() {
        viewModelScope.launch {
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


    private suspend fun getPagedTransactions(page: Int): Result<List<Transaction>> =  runCatching {
        transactionRepository.getTransactionHistory(
            page = page,
            pageSize = 20,
            TransactionFilterParams()
        )
    }

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
        sendEffect(TransactionHistoryEffect.NavigateToFilterBottomSheet)
    }

    override fun onNextPageRequested(){
        loadNextTransactions()
    }

    override fun onRetry() {
        loadNextTransactions()
    }
}