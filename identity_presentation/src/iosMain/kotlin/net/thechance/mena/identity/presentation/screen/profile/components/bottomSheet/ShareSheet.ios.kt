package net.thechance.mena.identity.presentation.screen.profile.components.bottomSheet

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(BetaInteropApi::class)
@Composable
actual fun ShareSheet(title: String, url: String, onDismiss: () -> Unit) {
    val nsTitle = NSString.create(string = title)
    val nsUrl = NSURL(string = url)

    val shareSheet = UIActivityViewController(
        activityItems = listOf(nsTitle, nsUrl),
        applicationActivities = null
    )

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

    rootViewController?.presentViewController(
        shareSheet,
        animated = true,
        completion = onDismiss
    )
}