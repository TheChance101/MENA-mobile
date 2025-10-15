package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.component.FaithSnackBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedAyahActionButtons
import net.thechance.mena.faith.presentation.feature.quran.surah.component.BasmalaHeader
import net.thechance.mena.faith.presentation.feature.quran.surah.component.SurahAppBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.UnifiedChunkText
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import net.thechance.mena.faith.presentation.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurahScreen(
    viewModel: SurahViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is SurahScreenEffect.NavigateBack -> navController.navigateUp()
            is SurahScreenEffect.ShareAyah -> {}
            is SurahScreenEffect.NavigateToSearchScreen -> {
                navController.navigate(
                    Route.SearchRoute(
                        effect.surahId,
                        effect.surahName
                    )
                )
            }
        }
    }

    Content(
        state = uiState,
        listener = viewModel,
        snackBarState = snackBarState
    )
}

@Composable
private fun Content(
    state: SurahScreenState,
    listener: SurahInteractionListener,
    snackBarState: SnackBarState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SurahAppBar(
                surahName = state.surahName,
                onBackClick = listener::onBackClick,
                onSearchClick = listener::onSearchClick
            )
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                status = snackBarState.status,
                isVisible = snackBarState.isVisible,
                modifier = modifier.fillMaxWidth()
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface)
        ) {
            AyatOfSurah(
                listener = listener,
                state = state
            )

            AnimatedAyahActionButtons(
                state = state,
                listener = listener,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun AyatOfSurah(
    listener: SurahInteractionListener,
    state: SurahScreenState,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val ayahChunks = remember(state.ayatOfSurah) {
        state.ayatOfSurah.chunked(AYAT_PER_PAGE)
    }
    val preRenderedChunks = remember(ayahChunks) {
        ayahChunks.map { chunk ->
            buildAnnotatedString {
                chunk.forEachIndexed { index, ayah ->
                    append(ayah.content)
                    if (index < chunk.lastIndex) append(" ")
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState
    ) {
        if (state.isBasmalaVisible) {
            item {
                BasmalaHeader(
                    selectedAyahIndex = state.selectedAyahIndex,
                    onDismissActionButtons = listener::onDismissActionButtons
                )
            }
        }

        items(preRenderedChunks.size) { chunkIndex ->
            val chunkAyat = ayahChunks[chunkIndex]

            UnifiedChunkText(
                chunkAyat = chunkAyat,
                selectedAyahIndex = state.selectedAyahIndex,
                onLongPress = { ayah ->
                    listener.onAyahLongPress(
                        ayahContent = ayah.plainContent,
                        ayahIndex = ayah.number
                    )
                },
                onDismiss = { listener.onDismissActionButtons() }
            )
        }
    }
    HideAyahActionButtonsOnScroll(lazyListState, state, listener)
}

@Composable
private fun HideAyahActionButtonsOnScroll(
    lazyListState: LazyListState,
    state: SurahScreenState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(
        lazyListState,
        state.isAyahActionButtonsVisible
    ) {
        lazyListState.let { listState ->
            snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                if (isScrolling && state.isAyahActionButtonsVisible) {
                    listener.onDismissActionButtons()
                }
            }
        }
    }
}

private const val AYAT_PER_PAGE = 70
