package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import net.thechance.mena.identity.domain.util.AppTheme
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun SetSystemBarsAppearance(appTheme: AppTheme, isSystemInDarkTheme: Boolean) {
    SideEffect {
        dispatch_async(dispatch_get_main_queue()) {
            val theme =
                when (appTheme) {
                    AppTheme.LIGHT -> true
                    AppTheme.DARK -> false
                    AppTheme.SYSTEM -> !isSystemInDarkTheme
                }
            val style = if (theme) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent
            UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
        }
    }
}
