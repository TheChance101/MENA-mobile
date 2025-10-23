package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextLayoutResult
import kotlinx.coroutines.launch
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.navigation.LocalNavController
/**
 * ScrollInitializers.kt
 *
 * This file contains logic responsible for **initializing scroll position** when opening or restoring a Surah.
 *
 * Responsibilities:
 * - Automatically scrolls to the Ayah or chunk the user last viewed.
 * - Ensures smooth restoration of the reading position without flicker or delay.
 *
 * Components:
 * - [HandleInitialScroll] → Coordinates the steps required to restore initial scroll position.
 * - [HandleBackStackAyahHighlight] → Highlights an Ayah if returned from navigation back stack.
 * - [ScrollToInitialChunk] → Scrolls directly to the chunk containing the target Ayah.
 * - [AnimateScrollToExactAyahPosition] → Performs fine scroll animation to align precisely with the Ayah line.
 *
 * Notes:
 * - Runs only once when the Surah screen is first composed.
 * - Works together with [Scroll Tracking file] to maintain consistent reading progress.
 */

@Composable
fun HandleInitialScroll(
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
private fun HandleBackStackAyahHighlight(
    selectedAyahIndex: Int?,
    highlightAyah: (Int) -> Unit
) {
    val navController = LocalNavController.current

    LaunchedEffect(
        navController.currentBackStackEntry?.savedStateHandle?.get<Int>("ayahNumber"),
        selectedAyahIndex
    ) {
        val ayahNumberFromBackStack =
            navController.currentBackStackEntry?.savedStateHandle?.get<Int>("ayahNumber")

        ayahNumberFromBackStack?.let(highlightAyah)

        if (selectedAyahIndex == null) navController.currentBackStackEntry?.savedStateHandle?.remove<Int>("ayahNumber")
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
            if (lazyListState.firstVisibleItemIndex != scrollIndex) lazyListState.scrollToItem(scrollIndex)
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

    LaunchedEffect(textLayoutResult, initialAyahToScroll) {

        if (!shouldPerformPreciseScroll(textLayoutResult, initialAyahToScroll)
        ) return@LaunchedEffect

        val targetAyah = findAyahByNumber(chunkAyat = chunkAyat, ayahNumber = initialAyahToScroll!!)
            ?: return@LaunchedEffect

        val lineTopOffset = calculateAyahLineTopOffset(
            targetAyah = targetAyah,
            chunkAyat = chunkAyat,
            textLayoutResult = textLayoutResult!!
        )

        val scrollIndex = calculateScrollIndex(
            ayahNumber = targetAyah.number,
            isBasmalaVisible = isBasmalaVisible
        )

        coroutineScope.launch {
            lazyListState.animateScrollToItem(index = scrollIndex, scrollOffset = lineTopOffset)
            onInitialScrollDone()
        }
    }
}

private fun calculateScrollIndex(ayahNumber: Int, isBasmalaVisible: Boolean): Int {
    val chunkIndex = (ayahNumber - 1) / AYAT_PER_PAGE
    return if (isBasmalaVisible) chunkIndex + 1 else chunkIndex
}

private fun findAyahByNumber(chunkAyat: List<Ayah>, ayahNumber: Int): Ayah? =
    chunkAyat.find { it.number == ayahNumber }

private fun shouldPerformPreciseScroll(
    textLayoutResult: TextLayoutResult?,
    initialAyahToScroll: Int?
): Boolean = textLayoutResult != null && initialAyahToScroll != null

private fun calculateAyahLineTopOffset(
    targetAyah: Ayah,
    chunkAyat: List<Ayah>,
    textLayoutResult: TextLayoutResult
): Int {
    val ayahIndexInChunk = chunkAyat.indexOf(targetAyah)
    val charOffset = chunkAyat.take(ayahIndexInChunk).sumOf { it.content.length + 1 }

    return getAyahLineTopPosition(textLayoutResult, charOffset)
}

private fun getAyahLineTopPosition(
    textLayoutResult: TextLayoutResult,
    charPosition: Int
): Int {
    val lineIndex = textLayoutResult.getLineForOffset(charPosition)
    return textLayoutResult.getLineTop(lineIndex).toInt()
}