package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import net.thechance.mena.identity.domain.util.AppTheme
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
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

}
