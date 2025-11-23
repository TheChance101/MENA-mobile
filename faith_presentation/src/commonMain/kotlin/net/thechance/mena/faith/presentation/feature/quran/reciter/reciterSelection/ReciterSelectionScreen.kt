package net.thechance.mena.faith.presentation.feature.quran.reciter.reciterSelection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciters
import mena.faith_presentation.generated.resources.search_reciter
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.components.ReciterItem
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.reciter.component.SearchReciter
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchEmptyState
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecitersSelectionScreen(
    viewModel: ReciterSelectionViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            ReciterSelectionEffect.NavigateBack -> navController.popBackStack()
        }
    }

    Content(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun Content(
    state: RecitersSelectionUiState,
    listener: ReciterSelectionListener,
) {
    Scaffold(
        topBar = {
            SearchReciter(
                title = stringResource(Res.string.reciters),
                query = state.query,
                hint = state.queryHint,
                onQueryChange = listener::onQueryChange,
                clearQuery = listener::onClearQueryClick,
                onBackClick = listener::onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }) {
        if (state.query.isNotBlank() && state.searchResults.isEmpty()) {
            EmptySearchState(modifier = Modifier.fillMaxWidth())
        } else {
            ResultList(
                listener = listener,
                uiState = state,
                results = state.searchResults,
                modifier = Modifier.fillMaxWidth().padding(top = Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun EmptySearchState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        SearchEmptyState(
            subtitle = Res.string.search_reciter,
            isStartState = false,
            isResultsState = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing._16)
        )
    }
}

@Composable
private fun ResultList(
    uiState: RecitersSelectionUiState,
    listener: ReciterSelectionListener,
    results: List<ReciterSearchItemUi>,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.padding(top = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(results) { result ->
            ReciterItem(
                reciter = result.name,
                recitingType = result.recitingType,
                isDownloaded = false,
                onSelect = { listener.onSelectReciterClick(result.id) },
                isSelectReciter = result.id == uiState.selectedReciterId,
                isDownloadIconVisible = false,
                onDownloadClick = {}
            )
        }
    }
}


@Composable
@Preview
private fun SearchScreenPreview() {
    MenaTheme {
        QuranTheme {
            Content(
                state = RecitersSelectionUiState(
                    query = "s",
                    searchResults = listOf(
                        ReciterSearchItemUi(
                            id = 1,
                            name = "Mishary Rashid Alafasy",
                            recitingType = "Murattal",

                            ),
                        ReciterSearchItemUi(
                            id = 2,
                            name = "Abdul Basit Abdul Samad",
                            recitingType = "Mujawwad",
                        ),
                        ReciterSearchItemUi(
                            id = 3,
                            name = "Saad Al Ghamdi",
                            recitingType = "Murattal",
                        )
                    ),
                    lastSearchedQuery = "",
                    queryHint = "",
                    selectedReciterId = 1,
                ),
                listener = object : ReciterSelectionListener {
                    override fun onBackClick() {}
                    override fun onClearQueryClick() {}
                    override fun onQueryChange(query: String) {}
                    override fun onSelectReciterClick(reciterId: Int) {}
                })
        }
    }
}