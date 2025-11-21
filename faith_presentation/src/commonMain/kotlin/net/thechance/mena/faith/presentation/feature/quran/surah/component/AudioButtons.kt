package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.icon_curvy_right_direction
import mena.faith_presentation.generated.resources.icon_next
import mena.faith_presentation.generated.resources.icon_pause
import mena.faith_presentation.generated.resources.icon_play
import mena.faith_presentation.generated.resources.icon_previous
import mena.faith_presentation.generated.resources.icon_reciter
import mena.faith_presentation.generated.resources.icon_repeat
import mena.faith_presentation.generated.resources.next_ayah
import mena.faith_presentation.generated.resources.pause
import mena.faith_presentation.generated.resources.play
import mena.faith_presentation.generated.resources.previous_ayah
import mena.faith_presentation.generated.resources.read_all_surah
import mena.faith_presentation.generated.resources.reciters
import mena.faith_presentation.generated.resources.repeat
import mena.faith_presentation.generated.resources.surah_ayah_format
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.utils.extentions.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AudioButtons(
    surahName: String,
    ayahNumber: Int,
    isPlaying: Boolean,
    onReciterClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onTilawahClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(elevation = 12.dp)
            .clip(RoundedCornerShape(bottomEnd = Theme.radius.md, bottomStart = Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceLow),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(Res.string.surah_ayah_format, surahName, ayahNumber),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._12)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Theme.spacing._16)
                .padding(bottom = Theme.spacing._12),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.icon_reciter),
                contentDescription = stringResource(Res.string.reciters),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(24.dp)
                    .noRippleClickable(onClick = onReciterClick)
            )

            Row(
                modifier = modifier
                    .background(
                        Theme.colorScheme.background.surface,
                        RoundedCornerShape(Theme.radius.sm)
                    )
                    .padding(vertical = Theme.spacing._4, horizontal = Theme.spacing._12),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._32),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.icon_previous),
                    contentDescription = stringResource(Res.string.previous_ayah),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(24.dp)
                        .noRippleClickable(onClick = onPreviousClick)
                )

                Icon(
                    painter = painterResource(
                        if (isPlaying) Res.drawable.icon_pause else Res.drawable.icon_play
                    ),
                    contentDescription = stringResource(
                        if (isPlaying) Res.string.pause else Res.string.play
                    ),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(24.dp)
                        .noRippleClickable(onClick = onPlayPauseClick)
                )

                Icon(
                    painter = painterResource(Res.drawable.icon_next),
                    contentDescription = stringResource(Res.string.next_ayah),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(24.dp)
                        .noRippleClickable(onClick = onNextClick)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(Res.drawable.icon_repeat),
                    contentDescription = stringResource(Res.string.repeat),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.padding(end = Theme.spacing._12)
                        .size(24.dp)
                        .noRippleClickable(onClick = onRepeatClick)

                )

                Icon(
                    painter = painterResource(Res.drawable.icon_curvy_right_direction),
                    contentDescription = stringResource(Res.string.read_all_surah),
                    tint = Theme.colorScheme.primary.primary,
                    modifier = Modifier.size(24.dp)
                        .noRippleClickable(onClick = onTilawahClick)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            AudioButtons(
                surahName = "Al-Fatiha",
                ayahNumber = 1,
                isPlaying = true,
                onReciterClick = {},
                onPreviousClick = {},
                onPlayPauseClick = {},
                onNextClick = {},
                onRepeatClick = {},
                onTilawahClick = {}
            )
        }
    }
}
