package net.thechance.mena.wallet.presentation.screen.transaction_history

import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.UiState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

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
            onError = ::onGetTransactionHistoryError
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
        TODO("Not yet implemented")
    }

    override fun onTransactionCardClicked() {
        TODO("Not yet implemented")
    }

    override fun onShareClicked() {
        TODO("Not yet implemented")
    }

    override fun onFilterClicked() {
        TODO("Not yet implemented")
    }

}