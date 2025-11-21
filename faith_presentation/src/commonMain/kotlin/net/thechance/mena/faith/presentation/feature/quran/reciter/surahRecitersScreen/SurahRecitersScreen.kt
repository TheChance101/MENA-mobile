package net.thechance.mena.faith.presentation.feature.quran.reciter.surahRecitersScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun SurahRecitersScreen(
    viewModel: SurahRecitersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            SurahRecitersScreenEffect.NavigateBack -> navController.navigateUp()
        }
    }

    Content(uiState = uiState, listener = viewModel)
}
@Composable
private fun Content(
    uiState: SurahRecitersUiState,
    listener: SurahRecitersListener,
) {
    Scaffold(
        topBar = {
            SearchReciter(
                title = stringResource(Res.string.reciters),
                query = uiState.query,
                hint = uiState.queryHint,
                onQueryChange = listener::onQueryChange,
                clearQuery = listener::onClearQueryClick,
                onBackClick = listener::onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = Theme.spacing._16),
        ) {

            if (uiState.query.isNotBlank() && uiState.reciters.isEmpty()) {
                item {
                    SearchEmptyState(
                        subtitle = Res.string.search_reciter,
                        isStartState = false,
                        isResultsState = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            items(uiState.reciters) { reciter ->
                ReciterItem(
                    reciterId = reciter.id,
                    reciter = reciter.name,
                    recitingType = reciter.recitingType,
                    isDownloaded = reciter.isDownloaded,
                    onSelect = { listener.onSelectReciterClick(reciter.id) },
                    onDownloadClick = { listener.onDownloadClick(reciter.id) },
                    isSelectReciter = reciter.id == uiState.selectedReciterId,
                    isSwipeable = false,
                    isDownloadIconVisible = true,
                    onDelete = {}
                )
            }
        }
    }
}



@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            Content(
                uiState = SurahRecitersUiState(
                    reciters = listOf(
                        SurahRecitersUiState.ReciterUi(
                            id = 1,
                            name = "Mishary Rashid Alafasy",
                            recitingType = "Murattal",
                            isDownloaded = true
                        ),
                        SurahRecitersUiState.ReciterUi(
                            id = 2,
                            name = "Abdul Basit Abdul Samad",
                            recitingType = "Mujawwad",
                            isDownloaded = false
                        ),
                        SurahRecitersUiState.ReciterUi(
                            id = 3,
                            name = "Saad Al Ghamdi",
                            recitingType = "Murattal",
                            isDownloaded = true
                        )
                    ),
                    selectedReciterId = 1,
                    surahId = 1
                ),
                listener = object : SurahRecitersListener {
                    override fun onBackClick() {}
                    override fun onDownloadClick(reciterId: Int) {}
                    override fun onSelectReciterClick(reciterId: Int) {}
                    override fun onQueryChange(query: String) {}
                    override fun onClearQueryClick() {}
                }
            )
        }
    }
}
