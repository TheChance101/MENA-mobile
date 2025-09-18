package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.hafs
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.feature.quran.surah.SurahScreenState
import org.jetbrains.compose.resources.Font

@Composable
internal fun createClickableAyahText(
    ayatOfSurah: List<SurahScreenState.AyahUiState>,
    selectedAyahIndex: Int?
): AnnotatedString {
    return buildAnnotatedString {
        ayatOfSurah.forEachIndexed { index, aya ->
            val ayahTextColor = getAyahTextColor(selectedAyahIndex, index)

            pushStringAnnotation(tag = "AYAH", annotation = index.toString())

            withStyle(
                style = SpanStyle(
                    color = ayahTextColor,
                    fontFamily = FontFamily(Font(Res.font.hafs))
                )
            ) {
                append(aya.content)
                if (index < ayatOfSurah.size - 1) append(" ")
            }

            pop()
        }
    }
}

@Composable
private fun getAyahTextColor(selectedAyahIndex: Int?, currentIndex: Int): Color {
    return when (selectedAyahIndex) {
        null -> Theme.colorScheme.shadePrimary
        currentIndex -> Theme.colorScheme.shadePrimary
        else -> Theme.colorScheme.shadeTertiary
    }
}
