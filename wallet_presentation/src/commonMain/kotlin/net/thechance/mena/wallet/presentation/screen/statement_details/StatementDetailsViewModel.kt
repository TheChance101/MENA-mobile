package net.thechance.mena.wallet.presentation.screen.statement_details

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.base.UiState
import net.thechance.mena.wallet.presentation.utils.PdfHandler
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class StatementDetailsViewModel(
    @Provided private val pdfHandler: PdfHandler,
    @Provided private val statementLocation: StorageLocation,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<StatementDetailsScreenState, StatementDetailsEffect>
    (StatementDetailsScreenState()), StatementDetailsInteractionListener {

    init {
        getStatementPdf(statementLocation)
    }
    private fun getStatementPdf(statementLocation: StorageLocation) {
        tryToExecute(
            onStart = ::onGetStatementPdfStart,
            callee = { pdfHandler.getPdfBytes(statementLocation) },
            onSuccess = ::onGetStatementPdfSuccess,
            onError = ::onGetStatementPdfError,
            dispatcher = dispatcherIO
        )
    }

    override fun onNavigateBackClicked() {
       viewModelScope.launch (Dispatchers.IO){
           if (statementLocation is StorageLocation.Cache) pdfHandler.deletePdf(statementLocation)
            sendEffect(StatementDetailsEffect.NavigateBack)
        }
    }

    override fun onShareClicked() {
        if (currentState.statement is UiState.Success) {
            val statement = (currentState.statement as UiState.Success<ByteArray>).data
            sendEffect(StatementDetailsEffect.ShareStatement(statement))
        }
    }

    override fun onRetryClicked() {
        getStatementPdf(statementLocation)
    }

    private fun onGetStatementPdfStart() {
        updateState { it.copy(statement = UiState.Loading) }
    }

    private fun onGetStatementPdfSuccess(pdf: ByteArray?) {
        if (pdf == null) {
            updateState { it.copy(statement = UiState.Error(ErrorState.NoDataFound)) }
        } else {
            updateState { it.copy(statement = UiState.Success(pdf)) }
        }
    }

    private fun onGetStatementPdfError(error: ErrorState) {
        updateState { it.copy(statement = UiState.Error(error)) }
    }
}