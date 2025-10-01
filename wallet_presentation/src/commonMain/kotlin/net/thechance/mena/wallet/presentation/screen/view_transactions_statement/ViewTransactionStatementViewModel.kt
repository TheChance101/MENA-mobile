package net.thechance.mena.wallet.presentation.screen.view_transactions_statement

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.UiState
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ViewTransactionStatementViewModel(
    @Provided private val statementRepository: StatementRepository,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
):
    BaseViewModel<
            ViewTransactionStatementScreenState,
            ViewTransactionStatementEffect>
        (ViewTransactionStatementScreenState()),
    ViewTransactionStatementInteractionListener
{

    init {
        fetchLastStatement()
    }

    private fun fetchLastStatement() {
        tryToExecute(
            onStart = { updateState { currentState.copy(statement = UiState.Loading) }},
            callee = { statementRepository.getLastStatement() },
            onSuccess = ::onSuccessFetchLastStatement,
            onError = { e ->
                e.printStackTrace()
                updateState { it.copy(statement = UiState.Error(e)) }
            },
            dispatcher = dispatcherIO
        )
    }

    private fun onSuccessFetchLastStatement(statement: ByteArray?) {
        if (statement == null) {
            updateState { it.copy(statement = UiState.Error(Exception("No statement found"))) }
        } else {
            updateState { it.copy(statement = UiState.Success(statement)) }
        }
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
}