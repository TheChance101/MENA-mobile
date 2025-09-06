package net.thechance.mena.designsystem.presentation.theme.color.scheme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorScheme(
    val brand: Brand,
    val primary: Primary,
    val secondary: Secondary,
    val border: Border,
    val background: Background,
    val shadePrimary: Color,
    val shadeSecondary: Color,
    val shadeTertiary: Color,
    val stroke: Color,
    val textDisabled: Color,
    val disabled: Color,
    val error: Color,
    val warning: Color,
    val success: Color
) {
    data class Brand(
        val brand: Color,
        val brandVariant: Color,
        val onBrand: Color
    )

    data class Primary(
        val primary: Color,
        val onPrimary: Color,
        val onPrimaryBody: Color,
        val onPrimaryHint: Color
    )

    data class Secondary(
        val secondary: Color,
        val secondaryText: Color,
        val secondaryVariant: Color
    )

    data class Border(
        val disabled: Color,
        val brand: Color,
        val error: Color,
        val success: Color
    )

    data class Background(
        val surfaceLow: Color,
        val surface: Color,
        val surfaceHigh: Color,
        val bgError: Color,
        val bgWarning: Color,
        val bgSuccess: Color
    )
}

internal val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }