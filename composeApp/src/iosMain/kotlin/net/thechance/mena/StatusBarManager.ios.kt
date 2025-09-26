package net.thechance.mena

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.setStatusBarStyle
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun SetStatusBarIconsDark() {
    SideEffect {
        dispatch_async(dispatch_get_main_queue()) {
            val style: UIStatusBarStyle =
                UIStatusBarStyleDarkContent
            UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
        }
    }
}