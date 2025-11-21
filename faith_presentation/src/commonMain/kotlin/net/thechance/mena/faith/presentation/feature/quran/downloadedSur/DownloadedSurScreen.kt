package net.thechance.mena.faith.presentation.feature.quran.downloadedSur

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.delete_surah
import mena.faith_presentation.generated.resources.delete_surah_dialog_message
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DeleteConfirmationDialog
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DownloadedSurAppBar
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.DownloadedSurahCard
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components.EmptyDownloadState
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadedSurScreen(viewModel: DownloadedSurViewModel = koinViewModel()) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            DownloadedSurEffect.NavigateBack -> navController.navigateUp()
            is DownloadedSurEffect.NavigateToRecitersScreen ->
                navController.navigate(Route.ReciterSelectionRoute)
            is DownloadedSurEffect.NavigateToDownloadedSurahReciterScreen -> {
                navController.navigate(
                    Route.DownloadedRecitersRoute(
                        surahId = effect.surahId,
                    )
                )
            }
        }
    }

    Content(
        uiState = state,
        snackBar = snackBarState,
        listener = viewModel,
    )
}

@Composable
private fun Content(
    uiState: DownloadedSurUiState,
    snackBar: SnackBarState,
    listener: DownloadedSurInteractionListener,
) {
    Scaffold(
        topBar = {
            DownloadedSurAppBar(
                onRecitersSettingsClick = { listener.onReciterSettingsClick() },
                onBackClick = listener::onBackClick,
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
                    showDialog = uiState.showDeleteConfirmationDialog,
                    onDeleteClick = listener::onConfirmDeleteDownloadedSurahClick,
                    onDismiss = listener::onDismissDeleteConfirmationDialog,
                    title = stringResource(Res.string.delete_surah),
                    message = stringResource(Res.string.delete_surah_dialog_message)
                )
            }
        }
    ) {
        if (uiState.surDetails.isEmpty())
            EmptyDownloadState()
        else
            DownloadedSurahList(
                surahItems = uiState.surDetails,
                listener = listener,
            )
    }
}

@Composable
fun DownloadedSurahList(
    surahItems: List<DownloadedSurUiState.SurahDetailsUiState>,
    listener: DownloadedSurInteractionListener,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._12,
        )
    ) {
        items(surahItems) { surah ->
            DownloadedSurahCard(
                suraDetails = surah,
                onDownloadedSurahClick = { listener.onDownloadedSurahClick(surah.id) },
                onDeleteDownloadedSurahClick = { listener.onDeleteSurahClick(surah.id) },
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(500),
                    fadeOutSpec = tween(500),
                )
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
                uiState = DownloadedSurUiState(
                    showDeleteConfirmationDialog = true,
                    surDetails = listOf(
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Surah.SurahOrder.AdDukhan,
                            "Al-Duha",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Surah.SurahOrder.AnNas,
                            "An-Nas",
                            listOf("Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Surah.SurahOrder.AlKahf,
                            "Al-Kahf",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Surah.SurahOrder.AshShams,
                            "Ash-Shams",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                    ),
                ),
                snackBar = SnackBarState(),
                listener =
                    object : DownloadedSurInteractionListener {
                        override fun onReciterSettingsClick() {}
                        override fun onDownloadedSurahClick(surahId: Int) {}
                        override fun onBackClick() {}
                        override fun onDeleteSurahClick(surahId: Int) {}
                        override fun onDismissDeleteConfirmationDialog() {}
                        override fun onConfirmDeleteDownloadedSurahClick() {}
                    },
            )
        }
    }
}
