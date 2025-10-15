package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mena.wallet_presentation.generated.resources.Res
import mena.wallet_presentation.generated.resources.file_missing
import mena.wallet_presentation.generated.resources.file_missing_description
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.Paginator
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import net.thechance.mena.wallet.presentation.utils.getPdfHandler
import org.jetbrains.compose.resources.getString
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class StatementsHistoryViewModel(
    @Provided private val statementRepository: StatementRepository,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<StatementsHistoryScreenState, StatementsHistoryEffect>
    (StatementsHistoryScreenState()), StatementsHistoryInteractionListener {

    init {
        loadNextStatements()
    }

    private fun loadNextStatements() {
        viewModelScope.launch(dispatcherIO) { paginator.loadNextItems() }
    }

    override fun onBackClicked() {
        sendEffect(effect = StatementsHistoryEffect.NavigateBack)
    }

    override fun onRetryLoadStatementsHistoryClicked() {
        loadNextStatements()
    }

    override fun onNextPageRequested() {
        loadNextStatements()
    }

    override fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem) {
        viewModelScope.launch(dispatcherIO) {
            val fileLocation = StorageLocation.Downloads(statement.fileName)

            val fileExists = getPdfHandler().checkIfPdfExists(fileLocation)

            if (fileExists) {
                sendEffect(
                    effect = StatementsHistoryEffect.NavigateToStatementDetails(
                        StorageLocation.Downloads(statement.fileName)
                    )
                )
            } else {
                showSnackBar(
                    title = getString(Res.string.file_missing),
                    message = getString(Res.string.file_missing_description),
                    isSuccess = false
                )
            }
        }
    }

    override fun onEditClicked() {
        updateState { it.copy(isEditMode = true) }
    }

    override fun onCancelEditModeClicked() {
        updateState { it.copy(isEditMode = false) }
    }

    override fun onDeleteClicked(id: Long) {
        tryToExecute(
            callee = { deleteStatementAndPdf(id) },
            onSuccess = { handleStatementRemoval(id) },
            onError = { errorState -> updateState { it.copy(errorState = errorState) } },
            dispatcher = dispatcherIO
        )
    }

    private suspend fun deleteStatementAndPdf(id: Long) {
        val statement = findStatementById(id)
        val fileLocation = StorageLocation.Downloads(statement.fileName)
        val pdfHandler = getPdfHandler()

        if (pdfHandler.checkIfPdfExists(fileLocation)) {
            pdfHandler.deletePdf(fileLocation)
        }

        statementRepository.deleteStatementById(id)
    }

    private fun findStatementById(id: Long): StatementsHistoryScreenState.StatementItem {
        return currentState.statements.find { it.id == id }
            ?: throw IllegalStateException("Statement not found")
    }

    private suspend fun handleStatementRemoval(id: Long) {
        delay(300)
        updateState { current ->
            val updatedList = current.statements.filter { it.id != id }
            current.copy(
                statements = updatedList,
                isStatementDeleted = false,
                isEditMode = updatedList.isNotEmpty()
            )
        }
        resetStatementDeletedAfterDelay()
    }

    private fun resetStatementDeletedAfterDelay() {
        viewModelScope.launch(dispatcherIO) {
            delay(100)
            updateState { it.copy(isStatementDeleted = false) }
        }
    }

    private fun onPaginationLoading(isLoading: Boolean) {
        updateState {
            it.copy(
                isLoading = isLoading && currentState.statements.isEmpty(),
                isPaginationLoading = isLoading && currentState.statements.isNotEmpty(),
            )
        }

        if (isLoading) {
            updateState { it.copy(errorState = null) }
        }
    }

    private suspend fun getPagedStatements(page: Int): List<Statement> {
        return statementRepository.getStatements(page = page, pageSize = PAGE_SIZE)
    }

    private fun onPaginationError(throwable: Throwable?) {
        when (throwable) {
            is NoInternetException -> updateState { it.copy(errorState = ErrorState.NoInternet) }
            else -> updateState { it.copy(errorState = ErrorState.Unknown) }
        }
    }

    private fun onPaginationSuccess(items: List<Statement>) {
        updateState {
            it.copy(
                statements = it.statements + items.map { statement -> statement.toUiState() },
                endOfPages = items.isEmpty()
            )
        }
    }

    private val paginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedStatements,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = ::onPaginationError,
            onSuccess = { result, newKey -> onPaginationSuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
    }

    private suspend fun showSnackBar(
        title: String,
        message: String,
        isSuccess: Boolean,
        durationMillis: Long = 3000L
    ) {
        updateState { oldState ->
            oldState.copy(
                snackBar = SnackBarState(
                    isVisible = true,
                    title = title,
                    message = message,
                    isSuccess = isSuccess
                )
            )
        }

        delay(durationMillis)

        hideSnackBar()
    }

    private fun hideSnackBar() {
        updateState { oldState -> oldState.copy(snackBar = oldState.snackBar.copy(isVisible = false)) }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }
}