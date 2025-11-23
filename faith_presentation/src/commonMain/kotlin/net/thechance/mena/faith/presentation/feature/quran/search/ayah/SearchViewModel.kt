package net.thechance.mena.faith.presentation.feature.quran.search.ayah

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.quran
import mena.faith_presentation.generated.resources.search_in_surah_hint
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.domain.repository.QuranRepository
import net.thechance.mena.faith.presentation.base.BaseViewModel
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.args.SearchArgs
import org.jetbrains.compose.resources.getString

class SearchViewModel(
    searchArgs: SearchArgs,
    private val repository: QuranRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<SearchUiState, SearchEffect>(
    SearchUiState(
        searchArgs.surahId,
    )
), SearchInteractionListener {

    private var searchJob: Job? = null

    init {
        initializeSearchHint()
    }

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        cancelPreviousSearch()

        if (isQueryTooShort(query)) return

        performSearchWithDelay(query)
    }

    override fun onSearchResultClick(surahId: Int, ayahId: Int) {
        if (uiState.value.surahId != null) {
            sendEffect(SearchEffect.NavigateBack(ayahId))
        } else {
            navigateToSurah(surahId, ayahId)
        }
    }

    override fun onClearQueryClick() {
        updateState { it.copy(query = "") }
    }

    override fun onBackClick() {
        sendEffect(SearchEffect.NavigateBack())
    }

    private fun cancelPreviousSearch() {
        searchJob?.cancel()
    }

    private fun performSearchWithDelay(query: String) {
        searchJob = tryToExecute(
            execute = { searchForAyah(query) },
            onSuccess = ::onSearchResultSuccess,
            dispatcher = dispatcher,
            onError = ::handleErrorSnackBar,
            delayMillis = SEARCH_DEBOUNCE_DELAY
        )
    }

    private fun isQueryTooShort(query: String): Boolean {
        val isTooShort = query.length < MIN_SEARCH_QUERY_LENGTH
        if (isTooShort) {
            clearSearchResults()
        }
        return isTooShort
    }

    private fun clearSearchResults() {
        updateState { it.copy(searchResults = emptyList()) }
    }

    private suspend fun searchForAyah(query: String): List<Ayah> {
        return if (uiState.value.surahId != null) {
            searchInCurrentSurah(query)
        } else {
            repository.searchForAyahInQuran(query)
        }
    }

    private suspend fun searchInCurrentSurah(query: String): List<Ayah> {
        return repository.searchForAyahInSurah(uiState.value.surahId!!, query)
    }

    private fun navigateToSurah(surahId: Int, ayahId: Int) {
        val surahName = getSurahName(surahId)
        sendEffect(SearchEffect.NavigateToSurah(surahId, ayahId, surahName))
    }

    private fun getSurahName(surahId: Int): String {
        return Surah.SurahOrder.entries[surahId - 1].name
    }

    private fun initializeSearchHint() {
        tryToExecute(
            execute = {
                val hint = buildSearchHint()
                updateState { it.copy(queryHint = hint) }
            }
        )
    }

    private suspend fun buildSearchHint(): String {
        val surahName = uiState.value.surahName ?: getString(Res.string.quran)
        return getString(Res.string.search_in_surah_hint, surahName)
    }

    private fun onSearchResultSuccess(ayat: List<Ayah>) {
        val searchResults = ayat.map { ayah ->
            ayah.toSearchResults(uiState.value.surahName)
        }
        updateState { it.copy(searchResults = searchResults) }
    }

    private companion object {
        const val MIN_SEARCH_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_DELAY = 1000L
    }
}