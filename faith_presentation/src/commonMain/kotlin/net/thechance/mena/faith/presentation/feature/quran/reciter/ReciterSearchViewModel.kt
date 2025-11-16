package net.thechance.mena.faith.presentation.feature.quran.reciter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.search_reciter
import net.thechance.mena.faith.domain.model.Reciter
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.reciter.args.ReciterArgs
import net.thechance.mena.faith.presentation.feature.quran.tilwah.toUi
import org.jetbrains.compose.resources.getString

class ReciterSearchViewModel(
    private val repository: QuranRepository,
    private val reciterArgs: ReciterArgs,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ReciterSearchUiState, ReciterSearchEffect>(
    ReciterSearchUiState()
), ReciterSearchInteractionListener {

    private var searchJob: Job? = null

    init {
        initializeSearchHint()
    }

    override fun onBackClick() = sendEffect(ReciterSearchEffect.NavigateBack)

    override fun onClearQueryClick() = updateState { it.copy(query = "") }

    override fun onQueryChange(query: String) {
        val lastSearchedQuery = uiState.value.query
        updateState { it.copy(query = query) }
        cancelPreviousSearch()

        if (isQueryTooShort(query)) return
        if (query == lastSearchedQuery) return

        performSearchWithDelay(query)
    }

    private fun cancelPreviousSearch() = searchJob?.cancel()

    private fun performSearchWithDelay(query: String) {
        searchJob = tryToExecute(
            execute = { searchForReciter(query) },
            onSuccess = ::onSearchResultSuccess,
            dispatcher = dispatcher
        )
    }

    private suspend fun searchForReciter(query: String): List<Reciter> =
        repository.searchForReciter(query)

    private suspend fun onSearchResultSuccess(reciters: List<Reciter>) {
        val surahId = reciterArgs.surahId ?: return

        val searchResults = reciters.map { reciter ->
            reciter.toUi(
                repository.isSurahAudioCached(surahId, reciter.id)
            )
        }

        updateState { it.copy(searchResults = searchResults) }
    }


    private fun isQueryTooShort(query: String): Boolean {
        val isTooShort = query.length < MIN_SEARCH_QUERY_LENGTH
        if (isTooShort) {
            clearSearchResults()
        }
        return isTooShort
    }

    private fun clearSearchResults() = updateState { it.copy(searchResults = emptyList()) }

    private fun initializeSearchHint() {
        tryToExecute(
            execute = {
                val hint = getString(Res.string.search_reciter)
                updateState { it.copy(queryHint = hint) }
            }
        )
    }

    private companion object {
        const val MIN_SEARCH_QUERY_LENGTH = 2
    }
}