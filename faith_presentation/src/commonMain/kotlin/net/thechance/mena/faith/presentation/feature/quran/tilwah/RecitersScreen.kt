package net.thechance.mena.faith.presentation.feature.quran.tilwah

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.ReciterItem
import net.thechance.mena.faith.presentation.feature.quran.tilwah.component.TilawahTopBar
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TilawahScreen(
    viewModel: TilawahViewModel = koinViewModel()
) {

    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            TilawahEffect.NavigateBack -> navController.navigateUp()
            TilawahEffect.NavigateToSearch -> navController.navigate(Route.ReciterSearch())
        }
    }
    Content(uiState = uiState, snackBar = snackBarState, listener = viewModel)
}

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
                status = snackBar.status
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = Theme.spacing._16),
        ) {

            items(uiState.reciters) { reciter ->
                ReciterItem(
                    reciterId = reciter.id,
                    reciter = reciter.name,
                    recitingType = reciter.recitingType,
                    isDownloaded = reciter.isDownloaded,
                    onSelect = {
                        listener.onSelectReciterClick(reciter.id)
                    },
                    onDownloadClick = {
                        listener.onDownloadClick(reciterId = reciter.id)
                    },
                    isSelectReciter = reciter.id == uiState.selectedReciterId,
                    isSwipeable = uiState.isSwipeable,
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
            snackBar = SnackBarState(),
            uiState = TilawahUiState(
                reciters = listOf(
                    ReciterUi(
                        id = 1,
                        name = "Mishary Rashid Alafasy",
                        recitingType = "Murattal",
                        isDownloaded = true
                    ),
                    ReciterUi(
                        id = 2,
                        name = "Abdul Basit Abdul Samad",
                        recitingType = "Mujawwad",
                        isDownloaded = false
                    ),
                    ReciterUi(
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
            }
        )
    }
}
