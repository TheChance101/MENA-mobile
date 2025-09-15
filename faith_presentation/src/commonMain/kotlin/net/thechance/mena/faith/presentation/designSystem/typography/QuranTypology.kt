package net.thechance.mena.faith.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.hafs
import org.jetbrains.compose.resources.Font

data class QuranTypology(
    val large: TextStyle,
    val medium: TextStyle,
) {

    companion object {
        fun create(fontFamily: FontFamily): QuranTypology {
            return QuranTypology(
                large = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 20.sp,
                    lineHeight = 46.sp,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Justify,
                ),
                medium = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.sp,
                )
            )
        }

        @Composable
        fun getFontFamily(): FontFamily = FontFamily(
            Font(Res.font.hafs, weight = FontWeight.Normal)
        )
    }
}

internal val LocalQuranTypography = staticCompositionLocalOf<QuranTypology> {
    error("No QuranTypography provided")
}