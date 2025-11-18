package net.thechance.mena.faith.presentation.feature.quran.search.ayah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.start_searching_subtitle_for_ayah
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchEmptyState
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchHeader
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchResultCard
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route.SurahDetailsRoute
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is SearchEffect.NavigateToSurah -> {
                navController.navigate(
                    SurahDetailsRoute(
                        surahId = effect.surahId,
                        ayahNumber = effect.ayahId
                    )
                )
            }

            is SearchEffect.NavigateBack -> {
                if (effect.ayahNumber != null) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("ayahNumber", effect.ayahNumber)
                }
                navController.navigateUp()
            }
        }
    }
    Content(
        state = state,
        snackBar = snackBarState,
        listener = viewModel
    )
}

@Composable
private fun Content(
    state: SearchUiState,
    snackBar: SnackBarState,
    listener: SearchInteractionListener
) {
    Scaffold(
        topBar = {
            SearchHeader(
                query = state.query,
                hint = state.queryHint,
                onQueryChange = listener::onQueryChange,
                clearQuery = listener::onClearQueryClick,
                onBackClick = listener::onBackClick,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4)
            )
        }, snakeBar = {
            FaithSnackBar(
                message = snackBar.message,
                isVisible = snackBar.isVisible,
                status = snackBar.status
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchEmptyState(
                isStartState = state.query.isBlank(),
                isResultsState = state.searchResults.isEmpty(),
                modifier = Modifier.fillMaxWidth().weight(1f),
                subtitle = Res.string.start_searching_subtitle_for_ayah
            )
            ResultList(
                isNotBlankQuery = state.query.isNotBlank(),
                isNotEmptyResult = state.searchResults.isNotEmpty(),
                results = state.searchResults,
                onSearchClick = listener::onSearchResultClick
            )
        }
    }
}

@Composable
private fun ResultList(
    isNotBlankQuery: Boolean,
    isNotEmptyResult: Boolean,
    results: List<SearchUiState.SearchResult>,
    onSearchClick: (surahId: Int, ayahId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shouldShowResults = isNotBlankQuery && isNotEmptyResult
    if (!shouldShowResults) return

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._12
        ),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(results) { result ->
            SearchResultCard(
                surahName = result.surahName,
                ayaNumber = result.number,
                ayaText = result.content,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Theme.radius.md))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .clickable(onClick = { onSearchClick(result.surahId, result.number) })
                    .padding(Theme.spacing._12)
            )
        }
    }
}


@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                state = SearchUiState(
                    surahId = null,
                    surahName = null,
                    query = "الرحمن",
                    queryHint = "ابحث في القرآن",
                    searchResults = listOf(
                        SearchUiState.SearchResult(
                            surahId = 1,
                            surahName = "الفاتحة",
                            number = 3,
                            content = "الرَّحْمَٰنِ الرَّحِيمِ",
                            plainContent = "الرحمن الرحيم"
                        ),
                        SearchUiState.SearchResult(
                            surahId = 55,
                            surahName = "الرحمن",
                            number = 1,
                            content = "الرَّحْمَٰنُ",
                            plainContent = "الرحمن"
                        ),
                        SearchUiState.SearchResult(
                            surahId = 2,
                            surahName = "البقرة",
                            number = 163,
                            content = "وَإِلَٰهُكُمْ إِلَٰهٌ وَاحِدٌ ۖ لَّا إِلَٰهَ إِلَّا هُوَ الرَّحْمَٰنُ الرَّحِيمُ",
                            plainContent = "والهكم اله واحد لا اله الا هو الرحمن الرحيم"
                        )
                    )
                ),
                snackBar = SnackBarState(),
                listener = object : SearchInteractionListener {
                    override fun onQueryChange(query: String) {}
                    override fun onClearQueryClick() {}
                    override fun onBackClick() {}
                    override fun onSearchResultClick(surahId: Int, ayahId: Int) {}
                }
            )
        }
    }
}
