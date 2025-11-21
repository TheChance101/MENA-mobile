package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.search_reciter
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.domain.usecase.SearchRecitersUseCase
import net.thechance.mena.faith.presentation.base.BaseViewModel
import org.jetbrains.compose.resources.getString

class ReciterSelectionViewModel(
    private val repository: QuranRepository,
    private val searchRecitersUseCase: SearchRecitersUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<RecitersSelectionUiState, ReciterSelectionEffect>(
    RecitersSelectionUiState()
), ReciterSelectionListener {

    private var searchJob: Job? = null

    init {
        initializeSearchHint()
        fetchAllReciters()
        updateDefaultReciter()
    }

    override fun onBackClick() = sendEffect(ReciterSelectionEffect.NavigateBack)

    override fun onClearQueryClick() {
        updateState { it.copy(query = "") }
        fetchAllReciters()
    }

    override fun onQueryChange(query: String) {
        val lastSearchedQuery = uiState.value.query
        updateState { it.copy(query = query) }
        cancelPreviousSearch()

        if (isQueryTooShort(query)) return
        if (query == lastSearchedQuery) return

        performSearchWithDelay(query)
    }

    override fun onSelectReciterClick(reciterId: Int) {
        tryToExecute(
            execute = { repository.saveDefaultReciter(reciterId) },
            onSuccess = { updateSelectedReciter(reciterId) },
        )
    }

    private fun updateDefaultReciter() {
        tryToExecute(
            execute = { repository.getDefaultReciter() },
            onSuccess = { id -> updateSelectedReciter(id.first()) },
        )
    }

    private fun updateSelectedReciter(reciterId: Int) {
        updateState { it.copy(selectedReciterId = reciterId) }
    }

    private fun cancelPreviousSearch() = searchJob?.cancel()

    private fun performSearchWithDelay(query: String) {
        searchJob = tryToExecute(
            execute = { searchForReciter(query) },
            onSuccess = ::onSearchResultSuccess,
            dispatcher = dispatcher
        )
    }

    private suspend fun searchForReciter(query: String): List<Reciter> {
        val allReciters = repository.getReciters()
        return searchRecitersUseCase(query, allReciters)
    }

    private fun onSearchResultSuccess(reciters: List<Reciter>) {
        val searchResults = reciters.map { reciter ->
            reciter.toUi()
        }

        updateState { it.copy(searchResults = searchResults) }
    }

    private fun isQueryTooShort(query: String): Boolean {
        val isTooShort = query.length < MIN_SEARCH_QUERY_LENGTH
        if (isTooShort) {
            fetchAllReciters()
        }
        return isTooShort
    }

    private fun initializeSearchHint() {
        tryToExecute(
            execute = {
                val hint = getString(Res.string.search_reciter)
                updateState { it.copy(queryHint = hint) }
            }
        )
    }

    private fun fetchAllReciters() {
        tryToExecute(
            execute = { repository.getReciters() },
            onSuccess = { allReciters ->
                val uiReciters = allReciters.map { reciter ->
                    reciter.toUi()
                }
                updateState { it.copy(searchResults = uiReciters) }
            },
            dispatcher = dispatcher
        )
    }

    private companion object {
        const val MIN_SEARCH_QUERY_LENGTH = 2
    }
}