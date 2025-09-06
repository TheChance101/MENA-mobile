package net.thechance.mena.designsystem.presentation.theme.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import net.thechance.mena.designsystem.presentation.theme.color.scheme.ColorScheme
import net.thechance.mena.designsystem.presentation.theme.color.scheme.DarkColorScheme
import net.thechance.mena.designsystem.presentation.theme.color.scheme.LightColorScheme
import net.thechance.mena.designsystem.presentation.theme.color.scheme.LocalColorScheme
import net.thechance.mena.designsystem.presentation.theme.radius.LocalRadius
import net.thechance.mena.designsystem.presentation.theme.radius.MenaRadius
import net.thechance.mena.designsystem.presentation.theme.radius.Radius
import net.thechance.mena.designsystem.presentation.theme.spacing.LocalSpacing
import net.thechance.mena.designsystem.presentation.theme.spacing.MenaSpacing
import net.thechance.mena.designsystem.presentation.theme.spacing.Spacing
import net.thechance.mena.designsystem.presentation.theme.typography.CreateThemeTypography
import net.thechance.mena.designsystem.presentation.theme.typography.LocalTypography
import net.thechance.mena.designsystem.presentation.theme.typography.MenaTypography
import net.thechance.mena.designsystem.presentation.theme.typography.Typography

@Composable
fun MenaTheme(
    isDarkTheme: Boolean, //todo system Theme or default theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val typography = CreateThemeTypography()

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalTypography provides MenaTypography,
        LocalSpacing provides MenaSpacing,
        LocalRadius provides MenaRadius,
        LocalTypography provides typography
    ) {
        content()
    }
}

object Theme {
    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = LocalColorScheme.current

    val typography: Typography
        @Composable @ReadOnlyComposable get() = LocalTypography.current

    val spacing: Spacing
        @Composable @ReadOnlyComposable get() = LocalSpacing.current

    val radius: Radius
        @Composable @ReadOnlyComposable get() = LocalRadius.current
}