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
actual fun SetStatusBarAppearance(appTheme: AppTheme) {
    val context = LocalContext.current
    SideEffect {
        (context as? ComponentActivity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = appTheme == AppTheme.LIGHT
            }
        }
    }
}

@Composable
actual fun SetNavigationBarAppearance(appTheme: AppTheme) {
    val context = LocalContext.current
    val color = Theme.colorScheme.background.surfaceLow
    SideEffect {
        val activity = context as? ComponentActivity ?: return@SideEffect
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        window.navigationBarColor = color.toArgb()
        controller.isAppearanceLightNavigationBars = appTheme == AppTheme.LIGHT
    }
}
