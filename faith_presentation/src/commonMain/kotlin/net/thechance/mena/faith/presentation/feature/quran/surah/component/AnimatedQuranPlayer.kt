package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AnimatedQuranPlayer(
    state: SurahUiState,
    surahId: Int,
    listener: SurahInteractionListener,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isPlayerVisible,
        enter = fadeIn(animationSpec = tween()),
        exit = fadeOut(animationSpec = tween()),
        modifier = modifier
    ) {
        QuranPlayer(
            surahName = state.surahName,
            onReciterClick = { listener.onReciterClick(surahId) },
            onPreviousClick = listener::onPreviousAyahClick,
            onPlayPauseClick = listener::onPlayPauseClick,
            onNextClick = listener::onNextAyahClick,
            onRepeatClick = listener::onRepeatAyahClick,
            ayahNumber = state.selectedAyahNumber ?: 1,
            isPlaying = state.isAyahSoundPlaying,
            reciterName = state.currentReciter.name,
            onCancelClick = listener::onClosePlayerClick,
            onTilawahClick = { listener.playSurah(surahNumber = surahId) }
        )
    }
}

@Preview
@Composable
private fun Preview(){
    MenaTheme {
        QuranTheme {
            AnimatedQuranPlayer(
                state = SurahUiState(
                    isPlayerVisible = true,
                    surahName = "Al-Fatiha",
                    selectedAyahNumber = 3,
                    isAyahSoundPlaying = true,
                ),
                surahId = 1,
                listener = object : SurahInteractionListener {
                    override fun onBackClick() {}
                    override fun onListenClick() {}
                    override fun onReciterClick(surahId: Int) {}
                    override fun onPreviousAyahClick() {}
                    override fun onDismissActionButtons() {}
                    override fun onShareClick(content: String) {}
                    override fun onBookmarkClick(ayahNumber: Int) {}
                    override fun onAyahLongPress(
                        ayahContent: String,
                        ayahIndex: Int
                    ) {}
                    override fun onSearchClick() {}
                    override fun onCopyClick(ayahContent: String) {}
                    override fun onInitialAyahScrolled() {}
                    override fun highlightAyah(ayahNumber: Int) {}
                    override fun updateContinueTilawah(ayahNumber: Int) {}
                    override fun onPlayPauseClick() {}
                    override fun onNextAyahClick() {}
                    override fun onRepeatAyahClick() {}
                    override fun onClosePlayerClick() {}
                    override fun playSurah(surahNumber: Int) {}
                    override fun onConfigrationChange() {}
                }
            )
        }
    }
}
