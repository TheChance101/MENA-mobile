package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun QuranPlayer(
    reciterName: String,
    surahName: String,
    ayahNumber: Int,
    isPlaying: Boolean,
    onReciterClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onTilawahClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReciterBox(
            reciterName = reciterName,
            onCancelClick = onCancelClick
        )
        AudioButtons(
            surahName = surahName,
            ayahNumber = ayahNumber,
            isPlaying = isPlaying,
            onReciterClick = onReciterClick,
            onPreviousClick = onPreviousClick,
            onPlayPauseClick = onPlayPauseClick,
            onNextClick = onNextClick,
            onRepeatClick = onRepeatClick,
            onTilawahClick = onTilawahClick
        )

    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            QuranPlayer(
                reciterName = "Maytham Al-Tammar",
                surahName = "Surah Name",
                ayahNumber = 1,
                isPlaying = true,
                onReciterClick = {},
                onPreviousClick = {},
                onPlayPauseClick = {},
                onNextClick = {},
                onRepeatClick = {},
                onCancelClick = {},
                onTilawahClick = {}
            )
        }
    }
}
