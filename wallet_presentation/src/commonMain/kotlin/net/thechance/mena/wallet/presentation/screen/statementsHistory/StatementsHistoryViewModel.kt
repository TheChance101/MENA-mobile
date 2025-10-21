@file:OptIn(ExperimentalUuidApi::class)

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
import mena.wallet_presentation.generated.resources.unknown_error_description
import mena.wallet_presentation.generated.resources.unknown_error_title
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.utils.Paginator
import net.thechance.mena.wallet.presentation.utils.PdfHandler
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class StatementsHistoryViewModel(
    @Provided private val statementRepository: StatementRepository,
    private val stringProvider: StringProvider,
    @Provided private val pdfHandler: PdfHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<StatementsHistoryScreenState, StatementsHistoryEffect>
    (StatementsHistoryScreenState()), StatementsHistoryInteractionListener {

    init {
        loadNextStatements()
    }

    private fun loadNextStatements() {
        viewModelScope.launch(dispatcher) { paginator.loadNextItems() }
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

    override fun onStatementCardClicked(
        statement: StatementsHistoryScreenState.StatementItem,
        onViewStatementAvailable: (isPdfFound: Boolean) -> Unit
    ) {
        val fileLocation = StorageLocation.Downloads(statement.fileName)

        tryToExecute(
            callee = { pdfHandler.checkIfPdfExists(fileLocation) },
            onSuccess = { fileExists ->
                if (fileExists) {
                    onViewStatementAvailable(true)
                    sendEffect(StatementsHistoryEffect.NavigateToStatementDetails(fileLocation))
                } else {
                    onViewStatementAvailable(false)
                    deleteNotFoundStatement(statement)
                }
            },
            onError = {
                onViewStatementAvailable(false)
                showSnackBar(
                    title = stringProvider.getString(Res.string.unknown_error_title),
                    message = stringProvider.getString(Res.string.unknown_error_description),
                    isSuccess = false
                )
            },
            dispatcher = dispatcher
        )
    }


    private fun deleteNotFoundStatement(statement: StatementsHistoryScreenState.StatementItem) {
        tryToExecute(
            callee = { statementRepository.deleteStatementById(statement.id) },
            onSuccess = { onDeleteNotFoundStatementSuccess(statement.id) },
            onError = { onDeleteNotFoundStatementError() },
            dispatcher = dispatcher
        )
    }

    private suspend fun onDeleteNotFoundStatementSuccess(id: Uuid) {
        delay(DELETE_DELAY_MS)

        removeStatementFromState(id = id)

        showSnackBar(
            title = stringProvider.getString(Res.string.file_missing),
            message = stringProvider.getString(Res.string.file_missing_description),
            isSuccess = false
        )
    }

    private suspend fun onDeleteNotFoundStatementError() {
        showSnackBar(
            title = stringProvider.getString(Res.string.unknown_error_title),
            message = stringProvider.getString(Res.string.unknown_error_description),
            isSuccess = false
        )
    }

    private fun removeStatementFromState(id: Uuid) {
        updateState { current ->
            current.copy(statements = current.statements.filter { it.id != id })
        }
    }

    override fun onEditClicked() {
        updateState { it.copy(isEditMode = true) }
    }

    override fun onCancelEditModeClicked() {
        updateState { it.copy(isEditMode = false) }
    }

    override fun onDeleteClicked(
        statement: StatementsHistoryScreenState.StatementItem,
        onDeleteComplete: (isSuccess: Boolean) -> Unit
    ) {
        tryToExecute(
            callee = { deleteStatementPdf(statement = statement) },
            onSuccess = {
                onDeleteStatementSuccess(
                    id = statement.id,
                    onDeleteComplete = onDeleteComplete
                )
            },
            onError = {
                showSnackBar(
                    title = stringProvider.getString(Res.string.unknown_error_title),
                    message = stringProvider.getString(Res.string.unknown_error_description),
                    isSuccess = false
                )
                onDeleteComplete(false)
            },
            dispatcher = dispatcher
        )
    }

    private suspend fun deleteStatementPdf(statement: StatementsHistoryScreenState.StatementItem) {
        val fileLocation = StorageLocation.Downloads(statement.fileName)

        if (pdfHandler.checkIfPdfExists(fileLocation)) {
            pdfHandler.deletePdf(fileLocation)
        }

        statementRepository.deleteStatementById(statement.id)
    }

    private suspend fun onDeleteStatementSuccess(
        id: Uuid,
        onDeleteComplete: (Boolean) -> Unit
    ) {
        delay(DELETE_DELAY_MS)

        removeStatementFromState(id = id)
        updateState { it.copy(isEditMode = it.statements.isNotEmpty()) }

        onDeleteComplete(true)
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
        const val DELETE_DELAY_MS = 300L
    }
}