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
import net.thechance.mena.wallet.domain.entity.Transaction
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.model.SnackBarState
import net.thechance.mena.wallet.presentation.screen.transaction_history.TransactionHistoryViewModel
import net.thechance.mena.wallet.presentation.utils.FileManager
import net.thechance.mena.wallet.presentation.utils.Paginator
import net.thechance.mena.wallet.presentation.utils.StorageLocation
import net.thechance.mena.wallet.presentation.utils.StringProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@KoinViewModel
class StatementsHistoryViewModel(
    @Provided private val statementRepository: StatementRepository,
    @Provided private val stringProvider: StringProvider,
    @Provided private val fileManager: FileManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<StatementsHistoryScreenState, StatementsHistoryEffect>
    (StatementsHistoryScreenState()), StatementsHistoryInteractionListener {

    private lateinit var paginator: Paginator<Int, List<Statement>>

    init {
        initializePaginator()
        loadNextStatements()
    }

    private fun initializePaginator() {
        paginator = Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = ::onPaginationLoading,
            onRequest = ::getPagedStatements,
            getNextKey = { currentKey, _ -> currentKey + 1 },
            onError = ::onPaginationError,
            onSuccess = { result, _ -> onPaginationSuccess(result) },
            endReached = { _, result -> result.isEmpty() || result.size < PAGE_SIZE }
        )
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

    override fun onStatementCardClicked(statement: StatementsHistoryScreenState.StatementItem) {
        val fileLocation = StorageLocation.Downloads(statement.fileName)

        tryToExecute(
            callee = { fileManager.checkIfFileExists(fileLocation) },
            onSuccess = { fileExists ->
                if (fileExists) {
                    sendEffect(StatementsHistoryEffect.NavigateToStatementDetails(fileLocation))
                } else {
                    deleteNotFoundStatement(statement)
                }
            },
            onError = {
                showSnackBar(
                    title = stringProvider.getString(Res.string.unknown_error_title),
                    message = stringProvider.getString(Res.string.unknown_error_description),
                    isSuccess = false
                )
            },
            dispatcher = dispatcher
        )
    }


    private suspend fun markStatementAsDeleted(id: Uuid) {
        updateState { current ->
            val updatedStatements = current.statements.map { statement ->
                if (statement.id == id) statement.copy(isDeleting = true) else statement
            }
            current.copy(statements = updatedStatements)
        }

        delay(ANIMATION_DELAY)

        updateState {
            val updatedStatements = it.statements.filter { statement -> statement.id != id }
            it.copy(statements = updatedStatements)
        }
    }

    private fun deleteNotFoundStatement(statement: StatementsHistoryScreenState.StatementItem) {
        tryToExecute(
            callee = { statementRepository.deleteStatementById(statement.id) },
            onSuccess = { onDeleteNotFoundStatementSuccess(statement.id) },
            onError = ::onDeleteNotFoundStatementError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onDeleteNotFoundStatementSuccess(id: Uuid) {
        markStatementAsDeleted(id = id)

        showSnackBar(
            title = stringProvider.getString(Res.string.file_missing),
            message = stringProvider.getString(Res.string.file_missing_description),
            isSuccess = false
        )
    }

    private suspend fun onDeleteNotFoundStatementError(error: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(Res.string.unknown_error_title),
            message = stringProvider.getString(Res.string.unknown_error_description),
            isSuccess = false
        )
    }

    override fun onEditClicked() {
        updateState { it.copy(isEditMode = true) }
    }

    override fun onCancelEditModeClicked() {
        updateState { it.copy(isEditMode = false) }
    }

    override fun onDeleteClicked(statement: StatementsHistoryScreenState.StatementItem) {
        tryToExecute(
            callee = { deleteStatementPdf(statement = statement) },
            onSuccess = { onDeleteStatementSuccess(id = statement.id) },
            onError = ::onDeleteStatementError,
            dispatcher = dispatcher
        )
    }

    private suspend fun deleteStatementPdf(statement: StatementsHistoryScreenState.StatementItem) {
        val fileLocation = StorageLocation.Downloads(statement.fileName)

        if (fileManager.checkIfFileExists(fileLocation)) {
            fileManager.deleteFile(fileLocation)
        }

        statementRepository.deleteStatementById(statement.id)
    }

    private suspend fun onDeleteStatementSuccess(id: Uuid) {
        markStatementAsDeleted(id = id)

        updateState { it.copy(isEditMode = it.statements.isNotEmpty()) }
    }

    private suspend fun onDeleteStatementError(error: ErrorState) {
        showSnackBar(
            title = stringProvider.getString(Res.string.unknown_error_title),
            message = stringProvider.getString(Res.string.unknown_error_description),
            isSuccess = false
        )
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
            else -> updateState { it.copy(errorState = ErrorState.UnknownError) }
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
        const val ANIMATION_DELAY = 500L
    }
}