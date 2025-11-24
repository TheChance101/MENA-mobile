package net.thechance.mena

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppTheme


@Composable
actual fun SetSystemBarsAppearance(appTheme: AppTheme, isSystemInDarkTheme: Boolean) {
    val context = LocalContext.current
    val navColor = Theme.colorScheme.background.surfaceLow

    SideEffect {
        val activity = context as? ComponentActivity ?: return@SideEffect
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        val theme =
            when (appTheme) {
                AppTheme.LIGHT -> true
                AppTheme.DARK -> false
                AppTheme.SYSTEM -> !isSystemInDarkTheme
            }
        controller.isAppearanceLightStatusBars = theme
        controller.isAppearanceLightNavigationBars = theme
        window.navigationBarColor = navColor.toArgb()

    }
}
