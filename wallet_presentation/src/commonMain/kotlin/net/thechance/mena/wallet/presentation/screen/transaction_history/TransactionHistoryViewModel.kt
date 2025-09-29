package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
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
    init {
        getTransactionHistory()
    }

    private fun getTransactionHistory() {
        tryToExecute(
            callee = { transactionRepository.getAllTransaction() },
            onStart = ::onGetTransactionHistoryStart,
            onSuccess = ::onGetTransactionHistorySuccess,
            onError = ::onGetTransactionHistoryError,
            dispatcher = Dispatchers.IO
        )
    }

    private fun onGetTransactionHistorySuccess(transactionHistory: List<Transaction>) {
        updateState { it.copy(history = transactionHistory.map { it -> it.toUi() }) }

    }

    private fun onGetTransactionHistoryStart() {
        updateState { it.copy(isLoading = true) }
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
        sendEffect(TransactionHistoryEffect.NavigateToFilterBottomSheet)
    }
}