package net.thechance.mena.core_chat.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.hafs
import org.jetbrains.compose.resources.Font

internal data class QuranTypography(
    val large: TextStyle,
    val medium: TextStyle,
) {

    companion object {
        fun create(fontFamily: FontFamily) = QuranTypography(
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

        @Composable
        fun getFontFamily() = FontFamily(
            Font(Res.font.hafs, weight = FontWeight.Normal)
        )
    }
}


internal val LocalQuranTypography = staticCompositionLocalOf<QuranTypography> {
    error("No QuranTypography provided")
}