package net.thechance.mena.faith.presentation.feature.quran.surah

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.SnackBarState
import net.thechance.mena.faith.presentation.component.FaithScaffold
import net.thechance.mena.faith.presentation.component.FaithSnackBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedAyahActionButtons
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AyatContent
import net.thechance.mena.faith.presentation.feature.quran.surah.component.BasmalaHeader
import net.thechance.mena.faith.presentation.feature.quran.surah.component.SurahAppBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.createClickableAyahText
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SurahScreen(
    surahId: Int,
    surahName: String,
    viewModel: SurahViewModel = koinViewModel(parameters = { parametersOf(surahId, surahName) })
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState by viewModel.snackBarState.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.uiEffect) { effect ->
        when (effect) {
            is SurahScreenEffect.NavigateBack -> navController.navigateUp()
            is SurahScreenEffect.ShareAyah -> {}
        }
    }

    Content(
        state = uiState,
        snackBarState = snackBarState,
        listener = viewModel
    )
}


@Composable
private fun Content(
    state: SurahScreenState,
    listener: SurahInteractionListener,
    snackBarState: SnackBarState,
) {
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            SurahAppBar(
                surahName = state.surahName, onBackClick = { listener.onBackClick() })
        },
        snakeBar = {
            FaithSnackBar(
                message = snackBarState.message,
                isVisible = snackBarState.isVisible,
                status = snackBarState.status,
                modifier = Modifier.fillMaxWidth()
            )
        }) {

        Box(
            Modifier.fillMaxSize()
        ) {
            AyatOfSurah(
                listener = listener, state = state, lazyListState = lazyListState
            )

            AnimatedAyahActionButtons(
                state = state,
                listener = listener,
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                    .padding(Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun AyatOfSurah(
    listener: SurahInteractionListener,
    state: SurahScreenState,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val annotatedText = createClickableAyahText(
        ayatOfSurah = state.ayatOfSurah,
        selectedAyahIndex = state.selectedAyahIndex
    )

    HideAyahActionButtonsOnScroll(
        lazyListState = lazyListState,
        state = state,
        listener = listener
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState
    ) {

        if (state.isBasmalaVisible)
            item {
                BasmalaHeader(
                    selectedAyahIndex = state.selectedAyahIndex,
                    onDismissActionButtons = { listener.onDismissActionButtons() })
            }

        item {
            AyatContent(
                annotatedText = annotatedText,
                state = state,
                ayat = state.ayatOfSurah,
                listener = listener
            )
        }
    }
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
