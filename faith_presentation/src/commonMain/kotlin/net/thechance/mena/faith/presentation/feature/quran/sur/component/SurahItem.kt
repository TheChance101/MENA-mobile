package net.thechance.mena.faith.presentation.feature.quran.sur.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import mena.faith_presentation.generated.resources.ayat
import mena.faith_presentation.generated.resources.ayat_count_format
import mena.faith_presentation.generated.resources.ic_al_fatihah
import mena.faith_presentation.generated.resources.ic_moshaf
import mena.faith_presentation.generated.resources.ic_surah_number_container
import mena.faith_presentation.generated.resources.madani
import mena.faith_presentation.generated.resources.makki
import mena.faith_presentation.generated.resources.moshaf_icon
import mena.faith_presentation.generated.resources.surah_arabic_name_icon
import mena.faith_presentation.generated.resources.surah_number_container_icon
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.feature.quran.sur.SurUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SurahItem(
    surah: SurUiState.SurahUiState,
    onClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Theme.radius.md))
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
            .clickable { onClick(surah.id) }
            .padding(vertical = Theme.spacing._8, horizontal = Theme.spacing._12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SurahNumberContainer(
            surahNumber = surah.surahOrder,
            modifier = Modifier.padding(end = Theme.spacing._12)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = surah.surahName,
                style = Theme.typography.label.medium,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier.padding(bottom = Theme.spacing._2)
            )

            SurahDetailsRow(ayatNumber = surah.ayatCount, isMakki = surah.isMakki)
        }

        Icon(
            painter = painterResource(resource = surah.arabicNameImg),
            contentDescription = stringResource(resource = Res.string.surah_arabic_name_icon),
            tint = Theme.colorScheme.shadePrimary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun SurahDetailsRow(
    ayatNumber: Int,
    isMakki: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_moshaf),
            tint = Theme.colorScheme.shadeSecondary,
            contentDescription = stringResource(resource = Res.string.moshaf_icon),
            modifier = Modifier
                .size(Theme.spacing._16)
                .padding(end = Theme.spacing._4)
        )

        Text(
            text = stringResource(
                resource = Res.string.ayat_count_format,
                ayatNumber,
                stringResource(resource = Res.string.ayat)
            ),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )

        Box(
            modifier = Modifier
                .padding(horizontal = Theme.spacing._8)
                .size(3.dp)
                .background(
                    color = Theme.colorScheme.shadeTertiary,
                    shape = RoundedCornerShape(Theme.radius.full)
                )
        )

        Text(
            text = if (isMakki) stringResource(resource = Res.string.makki)
            else stringResource(resource = Res.string.madani),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
    }
}

@Composable
private fun SurahNumberContainer(
    surahNumber: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_surah_number_container),
            contentDescription = stringResource(resource = Res.string.surah_number_container_icon),
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = surahNumber.twoDigitsMinimum(),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.secondary.secondary
        )
    }
}

private fun Int.twoDigitsMinimum(): String = this.toString().padStart(2, '0')

@Preview
@Composable
private fun Preview() {

    val mockSurah = SurUiState.SurahUiState(
        id = 1,
        surahOrder = 1,
        surahName = "Al Fatihah",
        arabicNameImg = Res.drawable.ic_al_fatihah,
        ayatCount = 7,
        isMakki = true
    )

    MenaTheme {
        QuranTheme {
            SurahItem(
                surah = mockSurah,
                onClick = {}
            )
        }
    }
}
