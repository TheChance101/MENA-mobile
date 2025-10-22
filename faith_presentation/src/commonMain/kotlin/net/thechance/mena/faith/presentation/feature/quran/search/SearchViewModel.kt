package net.thechance.mena.faith.presentation.feature.quran.search

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
import net.thechance.mena.faith.presentation.feature.quran.search.args.SearchArgs
import org.jetbrains.compose.resources.getString

class SearchViewModel(
    searchArgs: SearchArgs,
    private val repository: QuranRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<SearchUiState, SearchEffect>(
    SearchUiState(
        searchArgs.surahId,
        searchArgs.surahName
    )
),
    SearchInteractionListener {
    private var searchJob: Job? = null

    init {
        handleHint()
    }

    override fun onQueryChange(query: String) {
        updateState { it.copy(query = query) }
        searchJob?.cancel()
        if (query.length < LENGTH_OF_SHORTEST_WORD_IN_QURAN) {
            updateState { it.copy(searchResult = emptyList()) }
            return
        }
        searchJob = tryToExecute(
            delayMillis = 1000L,
            execute = {
                uiState.value.surahId?.let {
                    repository.searchForAyahInSurah(it, query)
                } ?: repository.searchForAyahInQuran(query)
            },
            onSuccess = ::onGetSearchResultSuccess,
            dispatcher = dispatcher
        )
    }

    override fun onClearQueryClick() {
        updateState { it.copy(query = "") }
    }

    override fun onBackClick() {
        sendEffect(SearchEffect.NavigateBack())
    }

    override fun onSearchResultClick(surahId: Int, ayahId: Int) {
        uiState.value.surahId?.let {
            sendEffect(SearchEffect.NavigateBack(ayahId))
        } ?: sendEffect(
            SearchEffect.NavigateToSurah(
                surahId,
                ayahId,
                Surah.SurahOrder.entries[surahId - 1].name
            )
        )
    }

    private fun handleHint() {
        tryToExecute({
            val hintPostfix = uiState.value.surahName ?: getString(Res.string.quran)
            val hint = getString(Res.string.search_in_surah_hint, hintPostfix)
            updateState { it.copy(hint = hint) }
        })
    }

    private fun onGetSearchResultSuccess(ayat: List<Ayah>) {
        updateState {
            it.copy(searchResult = ayat.map { ayah ->
                ayah.toSearchResult(it.surahName)
            })
        }
    }

    private companion object {
        const val LENGTH_OF_SHORTEST_WORD_IN_QURAN = 2
    }
}