package net.thechance.mena.faith.presentation.feature.quran.sur

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_al_fatihah
import mena.faith_presentation.generated.resources.sur
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.components.FaithSnackBar
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.sur.component.SurTopbar
import net.thechance.mena.faith.presentation.feature.quran.sur.component.SurahItem
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurScreen(
    viewModel: SurViewModel = koinViewModel(),
) {

    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is SurEffect.NavigateBack -> navController.navigateUp()
            is SurEffect.NavigateToBookmark -> navController.navigate(Route.BookmarksRoute)
            is SurEffect.NavigateToSurahDetails -> {
                navController.navigate(
                    Route.SurahDetailsRoute(surahId = effect.surahId)
                )
            }

            SurEffect.NavigateToSearch -> {
                navController.navigate(Route.SearchRoute())

            }
        }
    }

    Content(
        uiState = state,
        snackBar = snackBarState,
        listener = viewModel
    )
}

@Composable
private fun Content(
    uiState: SurUiState,
    snackBar: SnackBarState,
    listener: SurInteractionListener,
) {
    Scaffold(
        topBar = {
            SurTopbar(
                modifier = Modifier.padding(horizontal = Theme.spacing._16),
                onBackClick = { listener.onBackClick() },
                onBookmarkClick = { listener.onBookmarkClick() },
                onSearchClick = { listener.onSearchClick() }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Theme.spacing._16),
            contentPadding = PaddingValues(bottom = Theme.spacing._16),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        ) {

            item {
                Text(
                    text = stringResource(resource = Res.string.sur),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(bottom = Theme.spacing._4)
                )
            }

            items(uiState.sur) { surah ->
                SurahItem(
                    surah = surah,
                    onClick = {
                        listener.onSurahClick(
                            surah.id,
                        )
                    }
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
                uiState = SurUiState(
                    sur = listOf(
                        SurUiState.SurahUiState(
                            id = 1,
                            surahOrder = 1,
                            surahName = "Al Fatihah",
                            arabicNameImg = Res.drawable.ic_al_fatihah,
                            ayatCount = 7,
                            isMakki = true
                        ),
                        SurUiState.SurahUiState(
                            id = 1,
                            surahOrder = 1,
                            surahName = "Al Fatihah",
                            arabicNameImg = Res.drawable.ic_al_fatihah,
                            ayatCount = 7,
                            isMakki = true
                        ),
                        SurUiState.SurahUiState(
                            id = 1,
                            surahOrder = 1,
                            surahName = "Al Fatihah",
                            arabicNameImg = Res.drawable.ic_al_fatihah,
                            ayatCount = 7,
                            isMakki = true
                        )
                    )
                ),
                snackBar = SnackBarState(),
                listener = object : SurInteractionListener {
                    override fun onSurahClick(surahId: Int) = Unit
                    override fun onBackClick() = Unit
                    override fun onBookmarkClick() = Unit
                    override fun onSearchClick() {}
                }
            )
        }
    }
}
