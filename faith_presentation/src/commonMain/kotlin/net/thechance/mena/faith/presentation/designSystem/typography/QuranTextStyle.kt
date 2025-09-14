package net.thechance.mena.faith.presentation.designSystem.typography

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class QuranTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
)

internal val LocalQuranTypography = staticCompositionLocalOf<QuranTextStyle> {
    error("No QuranTypography provided")
}


