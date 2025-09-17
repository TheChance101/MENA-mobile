package net.thechance.mena.faith.presentation.feature.quran.surah

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import kotlinx.coroutines.flow.collectLatest
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_check_circle
import mena.faith_presentation.generated.resources.snack_bar_message
import mena.faith_presentation.generated.resources.snack_bar_title
import net.thechance.mena.designsystem.presentation.component.snackbar.SnackBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedAyahActionButtons
import net.thechance.mena.faith.presentation.feature.quran.surah.component.BismillahHeader
import net.thechance.mena.faith.presentation.feature.quran.surah.component.ClickableAyahText
import net.thechance.mena.faith.presentation.feature.quran.surah.component.SurahAppBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.createClickableAyahText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SurahScreen(
    viewModel: SurahViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val uiEffect = viewModel.uiEffect

    LaunchedEffect(Unit) {
        uiEffect.collectLatest { effect ->
            when (effect) {
                is SurahScreenEffect.NavigateBack -> {}
                is SurahScreenEffect.ShareAyah -> {}
            }
        }
    }

    Content(
        state = uiState,
        surahName = viewModel.surahName,
        listener = viewModel
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    state: SurahScreenState,
    listener: SurahInteractionListener,
    surahName: String
) {
    val lazyListState = rememberLazyListState()

    MenaTheme {
        Box(
            modifier = modifier.fillMaxSize()
                .background(Theme.colorScheme.background.surface)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {

            Column {
                SurahAppBar(
                    surahName = surahName ,
                    onBackClick = listener::onBackClick
                )

                AyatOfSurah(
                    listener = listener,
                    state = state,
                    lazyListState = lazyListState
                )
            }


            AnimatedAyahActionButtons(
                state = state,
                listener = listener,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(Theme.spacing._16)
            )
        }
        AnimatedVisibility (
            visible = state.isSnackBarVisible,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ){
            Box(
                modifier= Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ){
                SnackBar(
                    title = stringResource(Res.string.snack_bar_title),
                    message = stringResource(Res.string.snack_bar_message),
                    leadingIcon = painterResource(Res.drawable.ic_check_circle),
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            top = Theme.spacing._12 ,
                            start = Theme.spacing._16,
                            end =  Theme.spacing._16
                        )
                )
            }
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

    HandleScrollDismissEffect(
        lazyListState = lazyListState,
        state = state,
        listener = listener
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState
    ) {
        item {
            BismillahHeader(
                selectedAyahIndex = state.selectedAyahIndex,
                onDismissActionButtons = listener::onDismissActionButtons
            )
        }

        item {
            ClickableAyahText(
                annotatedText = annotatedText,
                state = state,
                ayat = state.ayatOfSurah,
                listener = listener,
                onTextLayoutResult = { listener.onTextLayoutChanged(it) })
        }
    }
}
@Composable
fun HandleScrollDismissEffect(
    lazyListState: LazyListState,
    state: SurahScreenState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(lazyListState) {
        lazyListState.let { listState ->
            snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                if (isScrolling && !state.isAyahActionButtonsVisible) {
                    listener.onDismissActionButtons()
                }
            }
        }
    }
}



