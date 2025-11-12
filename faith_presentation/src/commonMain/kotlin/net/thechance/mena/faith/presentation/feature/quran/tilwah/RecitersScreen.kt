package net.thechance.mena.faith.presentation.feature.quran.tilwah

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.delete_reciter
import mena.faith_presentation.generated.resources.delete_reciter_dialog_message
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DeleteConfirmationDialog
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.ReciterItem
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.TilawahTopBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun TilawahScreen(
    viewModel: TilawahViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            TilawahEffect.NavigateBack -> navController.navigateUp()
            TilawahEffect.NavigateToSearch -> navController.navigate(Route.ReciterSearch())
        }
    }

    Content(
        uiState = uiState,
        snackBar = snackBarState,
        listener = viewModel
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun Content(
    uiState: TilawahUiState,
    snackBar: SnackBarState,
    listener: TilawahInteractionListener,
) {
    Scaffold(
        topBar = {
            TilawahTopBar(
                onSearchClick = listener::onSearchClick,
                onBackClick = listener::onBackClick
            )
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBar.message,
                isVisible = snackBar.isVisible,
                status = snackBar.status,
            )
        },
        overlays = {
            dialog(
                isVisible = uiState.showDeleteConfirmationDialog,
            ) {
                DeleteConfirmationDialog(
                    title = stringResource(Res.string.delete_reciter),
                    message = stringResource(Res.string.delete_reciter_dialog_message),
                    showDialog = uiState.showDeleteConfirmationDialog,
                    onDeleteClick = listener::onConfirmDeleteReciterClick,
                    onDismiss = listener::onDismissDeleteConfirmationDialog,
                )
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = Theme.spacing._16),
        ) {

            items(uiState.reciters) { reciterDetails ->
                ReciterItem(
                    reciterId = reciterDetails.id,
                    reciter = reciterDetails.name,
                    recitingType = reciterDetails.recitingType,
                    isDownloaded = reciterDetails.isDownloaded,
                    onSelect = {
                        listener.onSelectReciterClick(reciterDetails.id)
                    },
                    onDownloadClick = {
                        listener.onDownloadClick(reciterId = reciterDetails.id)
                    },
                    isSelectReciter = reciterDetails.id == uiState.selectedReciterId,
                    onDeleteReciterClick = {
                        listener.onDeleteReciterClick(reciterDetails.id)
                    },
                    isSwipeable = uiState.isSwipeable
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    QuranTheme {
        Content(
            uiState = TilawahUiState(
                reciters = listOf(
                    TilawahUiState.ReciterUi(
                        id = 1,
                        name = "Mishary Rashid Alafasy",
                        recitingType = "Murattal",
                        isDownloaded = true
                    ),
                    TilawahUiState.ReciterUi(
                        id = 2,
                        name = "Abdul Basit Abdul Samad",
                        recitingType = "Mujawwad",
                        isDownloaded = false
                    ),
                    TilawahUiState.ReciterUi(
                        id = 3,
                        name = "Saad Al Ghamdi",
                        recitingType = "Murattal",
                        isDownloaded = false
                    )
                ),
                selectedReciterId = 1,
                surahId = 1
            ),
            listener = object : TilawahInteractionListener {
                override fun onBackClick() {}
                override fun onSearchClick() {}
                override fun onDownloadClick(reciterId: Int) {}
                override fun onSelectReciterClick(reciterId: Int) {}
                override fun onDeleteReciterClick(reciterId: Int) {}
                override fun onConfirmDeleteReciterClick() {}
                override fun onDismissDeleteConfirmationDialog() {}
            },
            snackBar = SnackBarState()
        )
    }
}
