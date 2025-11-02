package net.thechance.mena.faith.presentation.feature.downloadedSur

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_ad_duha
import mena.faith_presentation.generated.resources.ic_al_kahf
import mena.faith_presentation.generated.resources.ic_an_nas
import mena.faith_presentation.generated.resources.ic_ash_shams
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.downloadedSur.components.DownloadedSurAppBar
import net.thechance.mena.faith.presentation.feature.downloadedSur.components.DownloadedSurahCard
import net.thechance.mena.faith.presentation.navigation.LocalNavController
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
            DownloadedSurEffect.NavigateToRecitersScreen -> TODO("Navigate to reciters screen when done")
            is DownloadedSurEffect.NavigateToDownloadedSurahReciterScreen -> TODO("Navigate to downloaded surah reciters when done")
        }
    }

    Content(
        uiState = state,
        listener = viewModel,
    )
}

@Composable
private fun Content(
    uiState: DownloadedSurUiState,
    listener: DownloadedSurInteractionListener,
) {
    Scaffold(
        topBar = {
            DownloadedSurAppBar(
                onRecitersSettingsClick = listener::onReciterSettingsClick,
                onBackClick = listener::onBackClick,
            )
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding =
                PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                ),
        ) {
            items(uiState.surDetails) { downloadedSurah ->
                DownloadedSurahCard(
                    suraDetails = downloadedSurah,
                    onDeleteDownloadedSurahClick = {
                        listener.onDeleteDownloadedSurahClick(downloadedSurah.id)
                    },
                    modifier =
                        Modifier
                            .clickable(onClick = { listener.onDownloadedSurahClick(downloadedSurah.id) })
                            .animateItem(
                                fadeInSpec = tween(500),
                                fadeOutSpec = tween(500),
                            ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDownloadedSurScreen() {
    QuranTheme {
        Content(
            uiState =
                DownloadedSurUiState(
                    listOf(
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Res.drawable.ic_ad_duha,
                            "Al-Duha",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Res.drawable.ic_an_nas,
                            "An-Nas",
                            listOf("Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Res.drawable.ic_al_kahf,
                            "Al-Kahf",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                        DownloadedSurUiState.SurahDetailsUiState(
                            1,
                            Res.drawable.ic_ash_shams,
                            "Ash-Shams",
                            listOf("Al Minshawi", "Sudais"),
                        ),
                    ),
                ),
            listener =
                object : DownloadedSurInteractionListener {
                    override fun onReciterSettingsClick() {}

                    override fun onDownloadedSurahClick(surahId: Int) {}

                    override fun onDeleteDownloadedSurahClick(surahId: Int) {}

                    override fun onBackClick() {}
                },
        )
    }
}
