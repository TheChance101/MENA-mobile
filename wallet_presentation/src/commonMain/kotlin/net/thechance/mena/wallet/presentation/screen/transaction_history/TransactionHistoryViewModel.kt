package net.thechance.mena.wallet.presentation.screen.transaction_history

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.UiState
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
            callee = { transactionRepository.getTransactionHistory() },
            onStart = ::onGetTransactionDetailsStart,
            onSuccess = ::onGetTransactionHistorySuccess,
            onError = ::onGetTransactionHistoryError,
            dispatcher = Dispatchers.IO
        )
    }

    private fun onGetTransactionHistorySuccess(transactionHistory: List<Transaction>) {
        updateState { it.copy(history = UiState.Success(transactionHistory.map { it -> it.toUi() })) }

    }

    private fun onGetTransactionDetailsStart() {
        updateState { it.copy(history = UiState.Loading) }
    }

    private fun onGetTransactionHistoryError(throwable: Throwable) {
        updateState { it.copy(history = UiState.Error(throwable)) }
    }

    override fun onBackClicked() {
        sendEffect(TransactionHistoryEffect.NavigateBack)
    }

    override fun onTransactionCardClicked(id: Uuid) {
        sendEffect(TransactionHistoryEffect.NavigateToTransactionDetails(id))
    }

    override fun onShareClicked() {
        sendEffect(TransactionHistoryEffect.NavigateToExportTransaction)
    }

    override fun onFilterClicked() {
        sendEffect(TransactionHistoryEffect.NavigateToFilterBottomSheet)
    }
}