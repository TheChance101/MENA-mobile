package net.thechance.mena.wallet.presentation.screen.statement_details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.utils.FileManager
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class StatementDetailsViewModel(
    @Provided private val fileManager: FileManager,
    @Provided private val statementLocation: StorageLocation,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<StatementDetailsScreenState, StatementDetailsEffect>
    (StatementDetailsScreenState()), StatementDetailsInteractionListener {

    init {
        getStatementPdf(statementLocation)
    }

    private fun getStatementPdf(statementLocation: StorageLocation) {
        tryToExecute(
            onStart = ::onGetStatementPdfStart,
            onFinish = ::onGetStatementPdfFinish,
            callee = { fileManager.readFile(statementLocation) },
            onSuccess = ::onGetStatementPdfSuccess,
            onError = ::onGetStatementPdfError,
            dispatcher = dispatcher
        )
    }

    override fun onNavigateBackClicked() {
       viewModelScope.launch (Dispatchers.IO) {
           if (statementLocation is StorageLocation.Cache) fileManager.deleteFile(statementLocation)
            sendEffect(StatementDetailsEffect.NavigateBack)
        }
    }

    override fun onShareClicked() {
        if (currentState.statement.isNotEmpty() && currentState.errorState == null) {
            val statement = currentState.statement
            sendEffect(StatementDetailsEffect.ShareStatement(statement))
        }
    }

    override fun onRetryClicked() {
        getStatementPdf(statementLocation)
    }

    private fun onGetStatementPdfStart() {
        updateState { it.copy(isLoading = true) }
    }

    private fun onGetStatementPdfFinish() {
        updateState { it.copy(isLoading = false) }
    }

    private fun onGetStatementPdfSuccess(pdf: ByteArray?) {
        if (pdf == null) {
            updateState { it.copy(errorState = ErrorState.NoDataFound) }
        } else {
            updateState { it.copy(statement = pdf, errorState = null) }
        }
    }

    private fun onGetStatementPdfError(error: ErrorState) {
        updateState { it.copy(errorState = error) }
    }
}