package net.thechance.mena.designsystem.presentation.theme.typography

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

@Immutable
data class Typography(
    val display: Display,
    val headline: Headline,
    val title: Title,
    val body: Body,
    val label: Label
) {
    data class Display(
        val large: TextStyle,
        val medium: TextStyle,
        val small: TextStyle
    )

    data class Headline(
        val large: TextStyle,
        val medium: TextStyle,
        val small: TextStyle
    )

    data class Title(
        val large: TextStyle,
        val medium: TextStyle,
        val small: TextStyle
    )

    data class Body(
        val large: TextStyle,
        val medium: TextStyle,
        val small: TextStyle
    )

    data class Label(
        val large: TextStyle,
        val medium: TextStyle,
        val small: TextStyle
    )
}

val LocalTypography = staticCompositionLocalOf { MenaTypography }