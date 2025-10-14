@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.wallet.presentation.screen.statementsHistory

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import net.thechance.mena.wallet.domain.entity.Statement
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.StatementRepository
import net.thechance.mena.wallet.presentation.base.BaseViewModel
import net.thechance.mena.wallet.presentation.base.ErrorState
import net.thechance.mena.wallet.presentation.utils.Paginator
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    override fun onStatementCardClicked(id: Long) {
        sendEffect(effect = StatementsHistoryEffect.NavigateToStatementDetails(id))
    }

    override fun onEditClicked() {
//        TODO("Not yet implemented")
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

    private companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_PAGE = 0
    }

}