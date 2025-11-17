package net.thechance.mena.admin_panel.presentation.designSystem.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.thechance.mena.admin_panel.presentation.utils.OS
import net.thechance.mena.admin_panel.presentation.utils.PlatformDetector
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.twemoji_mozilla
import org.jetbrains.compose.resources.Font

data class EmojiTypography(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle
) {
    companion object {
        @Composable
        fun getFontFamily(): FontFamily? {
            return when (PlatformDetector.currentOS) {
                OS.WINDOWS, OS.LINUX -> {
                    FontFamily(
                        Font(
                            resource = Res.font.twemoji_mozilla,
                            weight = FontWeight.Normal
                        )
                    )
                }
                OS.MACOS, OS.UNKNOWN -> null
            }
        }


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