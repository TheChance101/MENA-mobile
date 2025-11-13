package net.thechance.mena

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import net.thechance.mena.identity.domain.util.AppTheme


@Composable
actual fun SetStatusBarIconsColor(appTheme: AppTheme) {
    val context = LocalContext.current
    SideEffect {
        (context as? ComponentActivity)?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = appTheme == AppTheme.LIGHT
            }
        }
    }
}