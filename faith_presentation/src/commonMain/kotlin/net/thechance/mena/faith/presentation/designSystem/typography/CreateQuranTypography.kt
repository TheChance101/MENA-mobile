package net.thechance.mena.faith.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun createQuranTypography(): QuranTextStyle {
    val fontFamily = quranFontFamily()
    return QuranTextStyle(
        quran = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            lineHeight = 46.sp,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Justify,
        ),
        quranBookMark = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.sp,
        )
    )
}
