package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ayah_number
import mena.faith_presentation.generated.resources.continue_tilawah
import mena.faith_presentation.generated.resources.ic_arrow_right
import mena.faith_presentation.generated.resources.ic_quran_play
import mena.faith_presentation.generated.resources.navigate_next
import mena.faith_presentation.generated.resources.quran_kareem
import mena.faith_presentation.generated.resources.surah_al_fatiha
import mena.faith_presentation.generated.resources.tilawah
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.main.TilawahUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TilawahSection(
    tilawahUiState: TilawahUiState?,
    onContinueTilawahClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._12)
    ) {
        Text(
            text = stringResource(Res.string.tilawah),
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            modifier = Modifier.padding(bottom = Theme.spacing._8)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Theme.radius.md))
                .clickable { onContinueTilawahClick() }
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(Theme.spacing._8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardIcon(
                icon = painterResource(Res.drawable.ic_quran_play),
                contentDescription = stringResource(Res.string.quran_kareem)
            )
            Column(
                modifier = Modifier.padding(start = Theme.spacing._8)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Text(
                    text = tilawahUiState?.surahName
                        ?: stringResource(Res.string.surah_al_fatiha),
                    style = Theme.typography.label.medium,
                    color = Theme.colorScheme.shadePrimary
                )
                Text(
                    text = stringResource(
                        Res.string.ayah_number,
                        tilawahUiState?.ayahNumber ?: "1"
                    ),
                    style = Theme.typography.label.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }
            Text(
                text = stringResource(Res.string.continue_tilawah),
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.primary.primary,

                )
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = stringResource(Res.string.navigate_next),
                tint = Theme.colorScheme.primary.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            TilawahSection(
                tilawahUiState = TilawahUiState(
                    surahId = 1,
                    surahName = "Al-Fatihah",
                    ayahNumber = 3
                ),
                onContinueTilawahClick = {},
            )
        }
    }
}
