package net.thechance.mena.core_chat.presentation.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual fun openAppSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    val application = UIApplication.sharedApplication
    if (application.canOpenURL(url)) {
        application.openURL(url, emptyMap<Any?, Any?>(), null)
    }
}

