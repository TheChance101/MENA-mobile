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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.base.ObserveAsEffect
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.component.FaithSnackBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.AnimatedAyahActionButtons
import net.thechance.mena.faith.presentation.feature.quran.surah.component.BasmalaHeader
import net.thechance.mena.faith.presentation.feature.quran.surah.component.SurahAppBar
import net.thechance.mena.faith.presentation.feature.quran.surah.component.UnifiedChunkAyat
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
    val ayahChunks = remember(state.ayatOfSurah) { state.ayatOfSurah.chunked(AYAT_PER_PAGE) }
    val preRenderedChunks = rememberPreRenderedChunks(ayahChunks)
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var currentChunkAyat by remember { mutableStateOf(listOf<Ayah>()) }

    SetupScrollTracking(
        lazyListState = lazyListState,
        ayahChunks = ayahChunks,
        state = state,
        listener = listener,
        textLayoutResult = textLayoutResult,
        currentChunkAyat = currentChunkAyat
    )

    AyahList(
        modifier = modifier,
        lazyListState = lazyListState,
        state = state,
        ayahChunks = ayahChunks,
        preRenderedChunks = preRenderedChunks,
        listener = listener,
        textLayoutResult = textLayoutResult,
        onTextLayoutResultChange = { textLayoutResult = it },
        onChunkChanged = { currentChunkAyat = it }
    )
}


@Composable
private fun SetupScrollTracking(
    lazyListState: LazyListState,
    ayahChunks: List<List<Ayah>>,
    state: SurahScreenState,
    listener: SurahInteractionListener,
    textLayoutResult: TextLayoutResult?,
    currentChunkAyat: List<Ayah>
) {
    TrackContinueTilawahPosition(
        lazyListState = lazyListState,
        ayahChunks = ayahChunks,
        isBasmalaVisible = state.isBasmalaVisible,
        listener = listener,
        textLayoutResult = textLayoutResult
    )

    HandleInitialScroll(
        initialAyahToScroll = state.initialAyahToScroll,
        selectedAyahIndex = state.selectedAyahIndex,
        isBasmalaVisible = state.isBasmalaVisible,
        lazyListState = lazyListState,
        highlightAyah = listener::highlightAyah,
        chunkAyat = currentChunkAyat,
        textLayoutResult = textLayoutResult,
        onInitialScrollDone = listener::onInitialAyahScrolled
    )

    HideAyahActionButtonsOnScroll(lazyListState, state, listener)

    InitializeBasmalaTilawahPosition(state, listener)
}

@Composable
private fun TrackContinueTilawahPosition(
    lazyListState: LazyListState,
    ayahChunks: List<List<Ayah>>,
    isBasmalaVisible: Boolean,
    listener: SurahInteractionListener,
    textLayoutResult: TextLayoutResult?
) {
    var lastReportedAyahNumber by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(lazyListState, textLayoutResult) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }.collect { (firstVisibleItemIndex, scrollOffset) ->
            val chunkIndex = getChunkIndexFromVisibleItem(firstVisibleItemIndex, isBasmalaVisible)

            val currentTextLayout = textLayoutResult ?: return@collect

            if (canDetermineVisibleAyah(chunkIndex, ayahChunks)) {
                val chunk = ayahChunks[chunkIndex]
                val visibleAyahNumber = findFirstVisibleAyahInChunk(
                    chunk = chunk,
                    textLayoutResult = currentTextLayout,
                    scrollOffset = scrollOffset
                )

                if (isNewAyahVisible(visibleAyahNumber, lastReportedAyahNumber)) {
                    lastReportedAyahNumber = visibleAyahNumber
                    if (visibleAyahNumber != null)
                        listener.updateContinueTilawah(visibleAyahNumber)
                }
            }
        }
    }
}

@Composable
private fun HandleInitialScroll(
    initialAyahToScroll: Int?,
    selectedAyahIndex: Int?,
    isBasmalaVisible: Boolean,
    lazyListState: LazyListState,
    chunkAyat: List<Ayah>,
    textLayoutResult: TextLayoutResult?,
    highlightAyah: (Int) -> Unit,
    onInitialScrollDone: () -> Unit
) {
    HandleBackStackAyahHighlight(selectedAyahIndex, highlightAyah)
    ScrollToInitialChunk(initialAyahToScroll, isBasmalaVisible, lazyListState)
    AnimateScrollToExactAyahPosition(
        textLayoutResult = textLayoutResult,
        initialAyahToScroll = initialAyahToScroll,
        chunkAyat = chunkAyat,
        isBasmalaVisible = isBasmalaVisible,
        lazyListState = lazyListState,
        onInitialScrollDone = onInitialScrollDone
    )
}

@Composable
private fun HideAyahActionButtonsOnScroll(
    lazyListState: LazyListState,
    state: SurahScreenState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(lazyListState, state.isAyahActionButtonsVisible) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling && state.isAyahActionButtonsVisible)
                    listener.onDismissActionButtons()
            }
    }
}

@Composable
private fun InitializeBasmalaTilawahPosition(
    state: SurahScreenState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(state.isBasmalaVisible) {
        if (state.isBasmalaVisible)
            listener.updateContinueTilawah(state.ayatOfSurah.firstOrNull()?.number ?: 1)
    }
}

@Composable
private fun AyahList(
    modifier: Modifier,
    lazyListState: LazyListState,
    state: SurahScreenState,
    ayahChunks: List<List<Ayah>>,
    preRenderedChunks: List<AnnotatedString>,
    listener: SurahInteractionListener,
    textLayoutResult: TextLayoutResult?,
    onTextLayoutResultChange: (TextLayoutResult) -> Unit,
    onChunkChanged: (List<Ayah>) -> Unit
) {
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
            onChunkChanged(chunkAyat)

            UnifiedChunkAyat(
                chunkAyat = chunkAyat,
                selectedAyahIndex = state.selectedAyahIndex,
                onLongPress = { ayah ->
                    listener.onAyahLongPress(
                        ayahContent = ayah.plainContent,
                        ayahIndex = ayah.number
                    )
                },
                onDismiss = listener::onDismissActionButtons,
                textLayoutResult = textLayoutResult,
                onTextLayoutResultChange = onTextLayoutResultChange
            )
        }
    }
}

@Composable
private fun rememberPreRenderedChunks(ayahChunks: List<List<Ayah>>) =
    remember(ayahChunks) {
        ayahChunks.map { chunk ->
            buildAnnotatedString {
                chunk.forEachIndexed { index, ayah ->
                    append(ayah.content)
                    if (index < chunk.lastIndex) append(" ")
                }
            }
        }
    }


@Composable
private fun HandleBackStackAyahHighlight(
    selectedAyahIndex: Int?,
    highlightAyah: (Int) -> Unit
) {
    val navController = LocalNavController.current

    LaunchedEffect(
        navController.currentBackStackEntry?.savedStateHandle?.get<Int>("ayahNumber"),
        selectedAyahIndex
    ) {
        val ayahNumberFromBackStack = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>("ayahNumber")

        ayahNumberFromBackStack?.let(highlightAyah)

        if (selectedAyahIndex == null) {
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Int>("ayahNumber")
        }
    }
}

@Composable
private fun ScrollToInitialChunk(
    initialAyahToScroll: Int?,
    isBasmalaVisible: Boolean,
    lazyListState: LazyListState
) {
    LaunchedEffect(initialAyahToScroll) {
        initialAyahToScroll?.let { ayahNumber ->
            val scrollIndex = calculateScrollIndex(ayahNumber, isBasmalaVisible)
            lazyListState.scrollToItem(scrollIndex)
        }
    }
}

@Composable
private fun AnimateScrollToExactAyahPosition(
    textLayoutResult: TextLayoutResult?,
    initialAyahToScroll: Int?,
    chunkAyat: List<Ayah>,
    isBasmalaVisible: Boolean,
    lazyListState: LazyListState,
    onInitialScrollDone: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(
        textLayoutResult,
        initialAyahToScroll
    ) {
        if (!shouldPerformPreciseScroll(
                textLayoutResult,
                initialAyahToScroll
            )
        ) return@LaunchedEffect
        val targetAyah = findAyahByNumber(chunkAyat, initialAyahToScroll!!) ?: return@LaunchedEffect
        val lineTopOffset = calculateAyahLineTopOffset(targetAyah, chunkAyat, textLayoutResult!!)
        val scrollIndex = calculateScrollIndex(targetAyah.number, isBasmalaVisible)
        coroutineScope.launch {
            lazyListState.animateScrollToItem(
                index = scrollIndex,
                scrollOffset = lineTopOffset
            )
            onInitialScrollDone()
        }
    }
}

private fun getChunkIndexFromVisibleItem(visibleItemIndex: Int, isBasmalaVisible: Boolean): Int =
    if (isBasmalaVisible) visibleItemIndex - 1 else visibleItemIndex

private fun canDetermineVisibleAyah(
    chunkIndex: Int,
    ayahChunks: List<List<Ayah>>
): Boolean = chunkIndex in ayahChunks.indices

private fun isNewAyahVisible(
    currentAyahNumber: Int?,
    previousAyahNumber: Int?
): Boolean = currentAyahNumber != null && currentAyahNumber != previousAyahNumber

private fun findFirstVisibleAyahInChunk(
    chunk: List<Ayah>,
    textLayoutResult: TextLayoutResult,
    scrollOffset: Int
): Int? {
    var charPositionInChunk = 0

    for (ayah in chunk) {
        val ayahLineTop = getAyahLineTopPosition(
            textLayoutResult = textLayoutResult,
            charPosition = charPositionInChunk
        )

        if (ayahLineTop >= scrollOffset) return ayah.number

        charPositionInChunk += ayah.content.length + 1
    }

    return chunk.firstOrNull()?.number
}

private fun shouldPerformPreciseScroll(
    textLayoutResult: TextLayoutResult?,
    initialAyahToScroll: Int?
): Boolean = textLayoutResult != null && initialAyahToScroll != null

private fun findAyahByNumber(chunkAyat: List<Ayah>, ayahNumber: Int): Ayah? =
    chunkAyat.find { it.number == ayahNumber }

private fun calculateAyahLineTopOffset(
    targetAyah: Ayah,
    chunkAyat: List<Ayah>,
    textLayoutResult: TextLayoutResult
): Int {
    val ayahIndexInChunk = chunkAyat.indexOf(targetAyah)
    val charOffset = chunkAyat.take(ayahIndexInChunk).sumOf { it.content.length + 1 }

    return getAyahLineTopPosition(textLayoutResult, charOffset)
}

private fun calculateScrollIndex(ayahNumber: Int, isBasmalaVisible: Boolean): Int {
    val chunkIndex = (ayahNumber - 1) / AYAT_PER_PAGE
    return if (isBasmalaVisible) chunkIndex + 1 else chunkIndex
}

private fun getAyahLineTopPosition(
    textLayoutResult: TextLayoutResult,
    charPosition: Int
): Int {
    val lineIndex = textLayoutResult.getLineForOffset(charPosition)
    return textLayoutResult.getLineTop(lineIndex).toInt()
}

private const val AYAT_PER_PAGE = 70