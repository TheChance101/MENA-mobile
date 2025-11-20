package net.thechance.mena.faith.presentation.feature.quran.surah.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.navigation.compose.rememberNavController
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.domain.entity.Ayah
import net.thechance.mena.faith.presentation.components.getAyahTextStyle
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.faith.presentation.navigation.LocalNavController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun UnifiedChunkAyat(
    chunkAyat: List<Ayah>,
    selectedAyahIndex: Int?,
    textLayoutResult: TextLayoutResult?,
    onTextLayoutResultChange: (TextLayoutResult) -> Unit,
    onLongPress: (Ayah) -> Unit,
    onDismiss: () -> Unit
) {
    val styledText = buildAnnotatedString {
        chunkAyat.forEach { ayah ->
            val color = getAyahTextColor(selectedAyahIndex, ayah.number)
            pushStyle(SpanStyle(color = color))
            append(ayah.content)
            pop()
            append(" ")
        }
    }
    BasicText(
        text = styledText,
        style = getAyahTextStyle(),
        onTextLayout = { onTextLayoutResultChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
            .pointerInput(selectedAyahIndex) {
                detectTapGestures(onTap = { onDismiss() }, onLongPress = { offset ->
                    textLayoutResult?.let { layout ->
                        val ayahPosition = layout.getOffsetForPosition(offset)
                        val clickedAyahIndex = findClickedAyahIndexFromPosition(
                            ayat = chunkAyat, position = ayahPosition
                        )
                        if (clickedAyahIndex != -1) {
                            val ayah = chunkAyat[clickedAyahIndex]
                            onLongPress(ayah)
                        }
                    }
                })
            })
}

@Composable
private fun getAyahTextColor(selectedAyahIndex: Int?, currentIndex: Int): Color {
    return when (selectedAyahIndex) {
        null -> Theme.colorScheme.shadePrimary
        currentIndex -> Theme.colorScheme.shadeSecondary
        else -> Theme.colorScheme.shadeTertiary
    }
}

private fun findClickedAyahIndexFromPosition(ayat: List<Ayah>, position: Int): Int {
    var currentPosition = 0
    ayat.forEachIndexed { index, ayah ->
        val end = currentPosition + ayah.content.length
        if (position in currentPosition..end) return index
        currentPosition = end + 1
    }
    return -1
}


@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            CompositionLocalProvider(LocalNavController provides rememberNavController()) {
                val sampleChunk = listOf(
                    Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "بسم الله الرحمن الرحيم"),
                    Ayah(2, 1, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الحمد لله رب العالمين"),
                    Ayah(3, 1, "الرَّحْمَٰنِ الرَّحِيمِ", "الرحمن الرحيم")
                )

                UnifiedChunkAyat(
                    chunkAyat = sampleChunk,
                    selectedAyahIndex = 2,
                    textLayoutResult = null,
                    onTextLayoutResultChange = {},
                    onLongPress = {},
                    onDismiss = {}
                )
            }
        }
    }
}
