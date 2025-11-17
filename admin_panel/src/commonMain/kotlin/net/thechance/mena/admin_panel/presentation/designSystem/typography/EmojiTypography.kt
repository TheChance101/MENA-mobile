package net.thechance.mena.admin_panel.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
@Composable
expect fun getEmojiFontFamily(): FontFamily?

data class EmojiTypography(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle
) {
    companion object {
        fun create(fontFamily: FontFamily?): EmojiTypography {
            return EmojiTypography(
                large = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                ),
                medium = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 20.sp
                ),
                small = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp
                )
            )
        }
    }
}

val LocalEmojiTypography = staticCompositionLocalOf<EmojiTypography> {
    error("No EmojiTypography provided")
}