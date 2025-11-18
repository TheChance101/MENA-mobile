package net.thechance.mena.faith.presentation.feature.quran.reciter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import mena.faith_presentation.generated.resources.search_reciter
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchEmptyState
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchHeader
import net.thechance.mena.faith.presentation.feature.quran.tilwah.ReciterUi
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.ReciterItem
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReciterSearchScreen(
    viewModel: ReciterSearchViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            ReciterSearchEffect.NavigateBack -> navController.popBackStack()
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
    state: ReciterSearchUiState,
    snackBar: SnackBarState,
    listener: ReciterSearchInteractionListener
) {
    Scaffold(
        topBar = {
            SearchHeader(
                query = state.query,
                hint = state.queryHint,
                onQueryChange = listener::onQueryChange,
                clearQuery = listener::onClearQueryClick,
                onBackClick = listener::onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._4)
            )
        }, snakeBar = {
            FaithSnackBar(
                message = snackBar.message,
                isVisible = snackBar.isVisible,
                status = snackBar.status,
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchEmptyState(
                subtitle = Res.string.search_reciter,
                isStartState = state.query.isBlank(),
                isResultsState = state.searchResults.isEmpty(),
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
            ResultList(
                isNotBlankQuery = state.query.isNotBlank(),
                isNotEmptyResult = state.searchResults.isNotEmpty(),
                results = state.searchResults,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = Theme.spacing._16)

            )
        }
    }
}

@Composable
private fun ResultList(
    isNotBlankQuery: Boolean,
    isNotEmptyResult: Boolean,
    results: List<ReciterUi>,
    modifier: Modifier = Modifier
) {
    val shouldShowResults = isNotBlankQuery && isNotEmptyResult
    if (!shouldShowResults) return

    LazyColumn(
        modifier = modifier,
    ) {
        items(results) { result ->
            ReciterItem(
                reciterId = result.id,
                reciter = result.name,
                recitingType = result.recitingType,
                isDownloaded = result.isDownloaded,
                onSelect = {},
                onDownloadClick = {},
                isSelectReciter = false,
                isSwipeable = false,
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
                state = ReciterSearchUiState(),
                snackBar = SnackBarState(),
                listener = object : ReciterSearchInteractionListener {
                    override fun onBackClick() {}
                    override fun onClearQueryClick() {}
                    override fun onQueryChange(query: String) {}
                }
            )
        }
    }
}
