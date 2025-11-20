package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.identity.domain.util.AppTheme
import platform.Foundation.NSStringCompareOptions
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UITabBar
import platform.UIKit.UITabBarAppearance
import platform.UIKit.setStatusBarStyle
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun SetStatusBarAppearance(appTheme: AppTheme) {
    SideEffect {
        dispatch_async(dispatch_get_main_queue()) {
            val style: UIStatusBarStyle =
                if (appTheme == AppTheme.LIGHT) UIStatusBarStyleLightContent else UIStatusBarStyleDarkContent
            UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
        }
    }
}
@Composable
actual fun SetNavigationBarAppearance(appTheme: AppTheme) {
    SideEffect {
        dispatch_async(dispatch_get_main_queue()) {
            val appearance = UITabBarAppearance().apply {
                configureWithOpaqueBackground()
                backgroundColor = Color.Transparent.toUIColor()
            }
            UITabBar.appearance().standardAppearance = appearance

            val systemVersion = UIDevice.currentDevice.systemVersion.split(".").firstOrNull()?.toIntOrNull() ?: 0
            if (systemVersion >= 15) {
                UITabBar.appearance().scrollEdgeAppearance = appearance
            }
        }
    }
}

private fun Color.toUIColor(): UIColor {
    return UIColor.colorWithRed(
        red = this.red.toDouble(),
        green = this.green.toDouble(),
        blue = this.blue.toDouble(),
        alpha = this.alpha.toDouble()
    )
}