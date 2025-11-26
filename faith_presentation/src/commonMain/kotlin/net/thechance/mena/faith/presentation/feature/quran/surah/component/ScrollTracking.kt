package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextLayoutResult
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahUiState
/**
 * ScrollTracking.kt
 *
 * This file contains all logic related to **scroll tracking** within the Surah reading screen.
 *
 * Responsibilities:
 * - Continuously observes the [LazyListState] to determine which Ayah (verse) is currently visible.
 * - Notifies [SurahInteractionListener] whenever the visible Ayah changes.
 * - Hides Ayah action buttons automatically when the user starts scrolling.
 *
 * Components:
 * - [TrackContinueTilawahPosition] → Monitors scroll position and updates the current visible Ayah.
 * - [HideAyahActionButtonsOnScroll] → Dismisses UI action buttons while scrolling.
 *
 * Utility functions:
 * - [getFirstVisibleAyahInChunk] → Determines the first visible Ayah based on text layout and scroll offset.
 *
 * Lifecycle:
 * Runs continuously during Surah reading to keep the UI synchronized with the reader’s position.
 */

@Composable
fun SetupScrollTracking(
    lazyListState: LazyListState,
    ayahChunks: List<List<Ayah>>,
    state: SurahUiState,
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

    HideAyahActionButtonsOnScroll(lazyListState = lazyListState, state = state, listener = listener)
    InitializeBasmalaTilawahPosition(state = state, listener = listener)

    HandleInitialScroll(
        initialAyahToScroll = state.initialAyahToScroll,
        selectedAyahIndex = state.selectedAyahNumber,
        isBasmalaVisible = state.isBasmalaVisible,
        lazyListState = lazyListState,
        chunkAyat = currentChunkAyat,
        textLayoutResult = textLayoutResult,
        highlightAyah = listener::highlightAyah,
        onInitialScrollDone = listener::onInitialAyahScrolled
    )
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
            val layout = textLayoutResult ?: return@collect

            if (chunkIndex in ayahChunks.indices) {
                val chunk = ayahChunks[chunkIndex]
                val visibleAyahNumber = getFirstVisibleAyahInChunk(chunk, layout, scrollOffset)

                if (visibleAyahNumber != null && visibleAyahNumber != lastReportedAyahNumber) {
                    lastReportedAyahNumber = visibleAyahNumber
                    listener.updateContinueTilawah(visibleAyahNumber)
                }
            }
        }
    }
}

@Composable
private fun HideAyahActionButtonsOnScroll(
    lazyListState: LazyListState,
    state: SurahUiState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling && state.isAyahActionButtonsVisible) listener.onDismissActionButtons()
            }
    }
}

@Composable
private fun InitializeBasmalaTilawahPosition(
    state: SurahUiState,
    listener: SurahInteractionListener
) {
    LaunchedEffect(state.isBasmalaVisible) {
        if (state.isBasmalaVisible)
            listener.updateContinueTilawah(state.ayatOfSurah.firstOrNull()?.number ?: 1)
    }
}

private fun getChunkIndexFromVisibleItem(visibleItemIndex: Int, isBasmalaVisible: Boolean): Int =
    if (isBasmalaVisible) visibleItemIndex - 1 else visibleItemIndex

private fun getFirstVisibleAyahInChunk(
    chunk: List<Ayah>,
    textLayoutResult: TextLayoutResult,
    scrollOffset: Int
): Int? {
    var currentCharPosition = 0

    chunk.forEach { ayah ->
        val ayahLength = ayah.content.length
        val lineForOffset = textLayoutResult.getLineForOffset(currentCharPosition)
        val lineTop = textLayoutResult.getLineTop(lineForOffset)

        if (lineTop.toInt() >= scrollOffset) return ayah.number
        currentCharPosition += ayahLength + 1
    }

    return chunk.lastOrNull()?.number
}

const val AYAT_PER_PAGE = 70