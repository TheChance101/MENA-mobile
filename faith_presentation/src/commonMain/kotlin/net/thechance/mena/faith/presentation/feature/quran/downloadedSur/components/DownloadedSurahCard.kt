package net.thechance.mena.faith.presentation.feature.quran.downloadedSur.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.delete_surah
import mena.faith_presentation.generated.resources.ic_delete
import mena.faith_presentation.generated.resources.ic_play_circle
import mena.faith_presentation.generated.resources.ic_reciter_list
import mena.faith_presentation.generated.resources.play
import mena.faith_presentation.generated.resources.reciter_list
import mena.faith_presentation.generated.resources.surah_arabic_name_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Surah
import net.thechance.mena.faith.presentation.components.PlayButton
import net.thechance.mena.faith.presentation.components.SwappableCard
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.downloadedSur.DownloadedSurUiState
import net.thechance.mena.faith.presentation.feature.quran.sur.getSurahNameDrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DownloadedSurahCard(
    suraDetails: DownloadedSurUiState.SurahDetailsUiState,
    onDownloadedSurahClick: () -> Unit,
    onDeleteDownloadedSurahClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSwipedCardId by remember { mutableIntStateOf(-1) }
    SwappableCard(
        id = suraDetails.id,
        onClick = onDeleteDownloadedSurahClick,
        backgroundIcon = painterResource(Res.drawable.ic_delete),
        contentDescription = stringResource(Res.string.delete_surah),
        currentSwipedCardId = currentSwipedCardId,
        cardContent = { contentModifier ->
            CardContent(
                surahDetailsUiState = suraDetails,
                modifier = contentModifier
                    .background(
                        color = Theme.colorScheme.background.surfaceLow,
                        shape = RoundedCornerShape(Theme.radius.md),
                    )
                    .clip(
                        shape = RoundedCornerShape(Theme.radius.md),
                    )
                    .clickable(onClick = onDownloadedSurahClick),
            )
        },
        modifier = modifier,
        onSwipeStateChange = { newId -> currentSwipedCardId = newId }
    )
}

@Composable
private fun CardContent(
    surahDetailsUiState: DownloadedSurUiState.SurahDetailsUiState,
    modifier: Modifier = Modifier,
) {
    val surahNameImage = getSurahNameDrawableResource(surahDetailsUiState.arabicNameImg)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        PlayButton(
            painterIcon = painterResource(Res.drawable.ic_play_circle),
            contentDescription = stringResource(Res.string.play),
            modifier = Modifier.padding(end = Theme.spacing._12),
        )

        SurahDetails(
            surahDetailsUiState,
            modifier = Modifier.weight(1f),
        )

        Icon(
            painter = painterResource(surahNameImage),
            contentDescription = stringResource(Res.string.surah_arabic_name_icon),
            tint = Theme.colorScheme.primary.primary,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun SurahDetails(
    surahDetailsUiState: DownloadedSurUiState.SurahDetailsUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        modifier = modifier,
    ) {

        Text(
            text = surahDetailsUiState.surahName,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        ) {

            Icon(
                painter = painterResource(Res.drawable.ic_reciter_list),
                contentDescription = stringResource(Res.string.reciter_list),
                tint = Theme.colorScheme.shadeSecondary,
            )

            Text(
                text = surahDetailsUiState.recitersName.joinToString(", "),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadeSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 17.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDownloadedSuraItem() {
    MenaTheme {
        QuranTheme {
            DownloadedSurahCard(
                DownloadedSurUiState.SurahDetailsUiState(
                    1,
                    Surah.SurahOrder.AlFajr,
                    "Al-Duha",
                    listOf("Al Minshawi", "Sudais"),
                ),
                {},
                {},
            )
        }
    }
}
