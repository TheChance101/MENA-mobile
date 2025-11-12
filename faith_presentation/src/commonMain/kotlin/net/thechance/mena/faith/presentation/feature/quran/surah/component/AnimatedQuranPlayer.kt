package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahInteractionListener
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahUiState

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
            onTilawahClick = { listener.playSurah(surahId) }
        )
    }
}