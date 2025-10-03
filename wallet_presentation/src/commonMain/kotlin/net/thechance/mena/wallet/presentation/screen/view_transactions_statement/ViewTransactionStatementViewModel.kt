package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.base.UiState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ViewTransactionStatementViewModel(
    @Provided private val statementRepository: StatementRepository,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ViewTransactionStatementScreenState, ViewTransactionStatementEffect>
    (ViewTransactionStatementScreenState()), ViewTransactionStatementInteractionListener {

    init {
        fetchLastStatement()
    }

    private fun fetchLastStatement() {
        tryToExecute(
            onStart = { updateState { currentState.copy(statement = UiState.Loading) } },
            callee = { statementRepository.getStoredTransactionsPdf() },
            onSuccess = ::onSuccessFetchPdf,
            onError = ::onErrorFetchPdf,
            dispatcher = dispatcherIO
        )
    }

    private fun onSuccessFetchPdf(pdf: ByteArray?) {
        if (pdf == null) {
            updateState { it.copy(statement = UiState.Error(ErrorState.NoDataFound)) }
        } else {
            updateState { it.copy(statement = UiState.Success(pdf)) }
        }
    }

    private fun onErrorFetchPdf(error: ErrorState) {
        updateState { it.copy(statement = UiState.Error(error)) }
    }

    override fun onNavigateBackClicked() {
        sendEffect(ViewTransactionStatementEffect.NavigateBack)
    }

    override fun onShareClicked() {
        if (currentState.statement is UiState.Success) {
            val statement = (currentState.statement as UiState.Success<ByteArray>).data
            sendEffect(ViewTransactionStatementEffect.ShareStatement(statement))
        }
    }

    override fun onRetryClicked() {
        fetchLastStatement()
    }
}