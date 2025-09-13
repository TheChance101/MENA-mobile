package net.thechance.mena.designsystem.presentation.theme.color.palette

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorPalette(
    val navy: ColorScale,
    val coffee: ColorScale,
    val gray: ColorScale,
    val red: ColorScale,
    val yellow: ColorScale,
    val green: ColorScale
) {
    data class ColorScale(
        val shade50: Color,
        val shade100: Color,
        val shade200: Color,
        val shade300: Color,
        val shade400: Color,
        val shade500: Color,
        val shade600: Color,
        val shade700: Color,
        val shade800: Color,
        val shade900: Color
    )
}