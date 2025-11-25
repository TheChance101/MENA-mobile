package net.thechance.mena.designsystem.presentation.theme.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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
import net.thechance.mena.designsystem.presentation.theme.typography.LocalTypography
import net.thechance.mena.designsystem.presentation.theme.typography.Typography
import net.thechance.mena.designsystem.presentation.theme.typography.createThemeTypography
import net.thechance.mena.designsystem.presentation.util.AppLanguage
import net.thechance.mena.designsystem.presentation.util.AppTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi

@OptIn(InternalResourceApi::class, ExperimentalResourceApi::class)
@Composable
fun MenaTheme(
    language: String = AppLanguage.English.iso,
    appTheme: String = AppTheme.SYSTEM.name,
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val isFollowingSystemDarkMode = remember(appTheme, isSystemInDarkTheme) {
        appTheme == AppTheme.SYSTEM.name && isSystemInDarkTheme
    }

    val colorScheme = remember(appTheme, isFollowingSystemDarkMode) {
        when (appTheme) {
            AppTheme.LIGHT.name -> LightColorScheme
            AppTheme.DARK.name -> DarkColorScheme
            AppTheme.SYSTEM.name -> if (isSystemInDarkTheme) DarkColorScheme else LightColorScheme
            else -> LightColorScheme
        }
    }
    val typography = createThemeTypography(language)
    val layoutDirection = remember(language) {
        if (language == AppLanguage.Arabic.iso) LayoutDirection.Rtl else LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalSpacing provides MenaSpacing,
        LocalRadius provides MenaRadius,
        LocalTypography provides typography,
        LocalLayoutDirection provides layoutDirection,
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