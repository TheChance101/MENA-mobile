package net.thechance.mena.faith.presentation.feature.quran.search.ayah.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.aya
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.components.DotSeparator
import net.thechance.mena.faith.presentation.components.getAyahTextStyle
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchResultCard(
    surahName: String,
    ayaNumber: Int,
    ayaText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        SurahAndAyaInfo(
            surahName = surahName,
            ayaNumber = ayaNumber,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = ayaText,
            color = Theme.colorScheme.shadeSecondary,
            style = getAyahTextStyle().copy(lineHeight = 35.sp)
        )
    }
}

@Composable
private fun SurahAndAyaInfo(
    surahName: String,
    ayaNumber: Int,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = surahName,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
        DotSeparator()
        Text(
            text = stringResource(Res.string.aya, ayaNumber),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            SearchResultCard(
                surahName = "Al-Fatiha",
                ayaNumber = 5,
                ayaText = "اهدِنَــــا الصِّرَاطَ المُستَقِيمَ",
            )
        }
    }
}
