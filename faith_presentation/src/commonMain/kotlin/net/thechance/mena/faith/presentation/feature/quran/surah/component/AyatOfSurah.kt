package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahUiState
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AyatOfSurah(
    listener: SurahInteractionListener,
    state: SurahUiState,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val ayahChunks = remember(state.ayatOfSurah) { state.ayatOfSurah.chunked(AYAT_PER_PAGE) }
    val preRenderedChunks = rememberPreRenderedChunks(ayahChunks)
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var currentChunkAyat by remember { mutableStateOf(listOf<Ayah>()) }

    BoxWithConstraints(modifier) {
        LaunchedEffect(maxWidth) {
            listener.onConfigrationChange()

        }

        SetupScrollTracking(
            lazyListState = lazyListState,
            ayahChunks = ayahChunks,
            state = state,
            listener = listener,
            textLayoutResult = textLayoutResult,
            currentChunkAyat = currentChunkAyat
        )

        AyahList(
            modifier = Modifier.fillMaxWidth(),
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
}

@Composable
private fun AyahList(
    modifier: Modifier,
    lazyListState: LazyListState,
    state: SurahUiState,
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
                    selectedAyahIndex = state.selectedAyahNumber,
                    onDismissActionButtons = listener::onDismissActionButtons
                )
            }
        }

        items(preRenderedChunks.size) { chunkIndex ->
            val chunkAyat = ayahChunks[chunkIndex]
            onChunkChanged(chunkAyat)

            UnifiedChunkAyat(
                chunkAyat = chunkAyat,
                selectedAyahIndex = state.selectedAyahNumber,
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
private fun rememberPreRenderedChunks(ayahChunks: List<List<Ayah>>): List<AnnotatedString> {
    return remember(ayahChunks) {
        ayahChunks.map { chunk ->
            buildAnnotatedString {
                chunk.forEachIndexed { index, ayah ->
                    append(ayah.content)
                    if (index < chunk.lastIndex) append(" ")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            CompositionLocalProvider(LocalNavController provides rememberNavController()) {
                val sampleAyat = listOf(
                    Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "بسم الله الرحمن الرحيم"),
                    Ayah(2, 1, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الحمد لله رب العالمين"),
                    Ayah(3, 1, "الرَّحْمَٰنِ الرَّحِيمِ", "الرحمن الرحيم"),
                    Ayah(4, 1, "مَالِكِ يَوْمِ الدِّينِ", "مالك يوم الدين"),
                    Ayah(
                        5,
                        1,
                        "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                        "إياك نعبد وإياك نستعين"
                    ),
                    Ayah(6, 1, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "اهدنا الصراط المستقيم"),
                    Ayah(
                        7,
                        1,
                        "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                        "صراط الذين أنعمت عليهم غير المغضوب عليهم ولا الضالين"
                    )
                )

                val state = SurahUiState(
                    surahId = 1,
                    surahName = "Al-Fatiha",
                    ayatOfSurah = sampleAyat,
                    selectedAyahNumber = null,
                    isAyahActionButtonsVisible = true,
                    isBasmalaVisible = true,
                    initialAyahToScroll = null
                )


                val listener = object : SurahInteractionListener {
                    override fun onAyahLongPress(ayahContent: String, ayahIndex: Int) {}
                    override fun onSearchClick() {}
                    override fun onListenClick() {}
                    override fun onReciterClick(surahId: Int) {}
                    override fun onNextAyahClick() {}
                    override fun onPlayPauseClick() {}
                    override fun onRepeatAyahClick() {}
                    override fun onClosePlayerClick() {}
                    override fun onPreviousAyahClick() {}
                    override fun onBackClick() {}
                    override fun onDismissActionButtons() {}
                    override fun onBookmarkClick(ayahNumber: Int) {}
                    override fun onCopyClick(ayahContent: String) {}
                    override fun onShareClick(content: String) {}
                    override fun highlightAyah(ayahNumber: Int) {}
                    override fun updateContinueTilawah(ayahNumber: Int) {}
                    override fun playSurah(surahNumber: Int) {}
                    override fun onConfigrationChange() {}
                    override fun onInitialAyahScrolled() {}
                }

                AyatOfSurah(
                    listener = listener,
                    state = state,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
