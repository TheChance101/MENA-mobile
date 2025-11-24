package net.thechance.mena.faith.presentation.feature.quran.reciter.downloadedReciters

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.reciters
import mena.faith_presentation.generated.resources.remove_audio
import mena.faith_presentation.generated.resources.remove_audio_message
import mena.faith_presentation.generated.resources.search_reciter
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.components.ReciterItem
import net.thechance.mena.faith.presentation.components.SwappableCard
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DeleteConfirmationDialog
import net.thechance.mena.faith.presentation.feature.quran.reciter.component.SearchReciter
import net.thechance.mena.faith.presentation.feature.quran.search.ayah.component.SearchEmptyState
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadedRecitersScreen(
    viewModel: DownloadedRecitersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            DownloadedRecitersEffect.NavigateBack -> navController.navigateUp()
        }
    }

    Content(uiState = uiState, listener = viewModel)
}

@Composable
private fun Content(
    uiState: DownloadedRecitersUiState,
    listener: DownloadedRecitersListener,
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
        },
        overlays = {
            dialog(
                isVisible = uiState.isDeleteConfirmationDialogVisible
            ) {
                DeleteConfirmationDialog(
                    showDialog = uiState.isDeleteConfirmationDialogVisible,
                    onDeleteClick = listener::onConfirmDeleteReciterClick,
                    onDismiss = listener::onDismissDeleteDialog,
                    title = stringResource(Res.string.remove_audio),
                    message = stringResource(Res.string.remove_audio_message)
                )
            }
        }
    ) {

        if (uiState.query.isNotBlank() && uiState.reciters.isEmpty())
            EmptyRecitersContent()
        else
            RecitersListContent(uiState = uiState, listener = listener)
    }
}

@Composable
fun EmptyRecitersContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
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
fun RecitersListContent(
    uiState: DownloadedRecitersUiState,
    listener: DownloadedRecitersListener
) {
    var currentSwipedCardId by remember { mutableIntStateOf(-1) }

    LazyColumn(
        modifier = Modifier.padding(top = Theme.spacing._16),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            count = uiState.reciters.size
        ) { index ->
            uiState.reciters[index].let {
                SwappableCard(
                    id = it.id,
                    onClick = { listener.onDeleteReciterAudioClick(it.id) },
                    currentSwipedCardId = currentSwipedCardId,
                    onSwipeStateChange = { newId -> currentSwipedCardId = newId },
                    cardContent = { contentModifier ->
                        ReciterItem(
                            reciter = it.name,
                            recitingType = it.recitingType,
                            onSelect = { listener.onSelectReciterClick(it.id) },
                            isDownloaded = false,
                            onDownloadClick = {},
                            isSelectReciter = false,
                            isDownloadIconVisible = false,
                            modifier = contentModifier,
                        )
                    },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(500),
                        fadeOutSpec = tween(500)
                    )
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
                uiState = DownloadedRecitersUiState(
                    reciters = listOf(
                        DownloadedReciterItemUi(
                            id = 1,
                            name = "Mishary Rashid Alafasy",
                            recitingType = "Murattal",
                            isDownloaded = true
                        ),
                        DownloadedReciterItemUi(
                            id = 2,
                            name = "Abdul Basit Abdul Samad",
                            recitingType = "Mujawwad",
                            isDownloaded = false
                        ),
                        DownloadedReciterItemUi(
                            id = 3,
                            name = "Saad Al Ghamdi",
                            recitingType = "Murattal",
                            isDownloaded = false
                        )
                    ),
                    reciterId = 1,
                    surahId = 1
                ),
                listener = object : DownloadedRecitersListener {
                    override fun onBackClick() {}
                    override fun onQueryChange(query: String) {}
                    override fun onClearQueryClick() {}
                    override fun onSelectReciterClick(reciterId: Int) {}
                    override fun onDeleteReciterAudioClick(reciterId: Int) {}
                    override fun onConfirmDeleteReciterClick() {}
                    override fun onDismissDeleteDialog() {}
                }
            )
        }
    }
}
