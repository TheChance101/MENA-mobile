package net.thechance.mena.identity.presentation.util

import net.thechance.mena.identity.domain.exception.CannotOpenSettingsException
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

fun openNSUrl(string: String) {
    val settingsUrl: NSURL = NSURL.URLWithString(string)!!
    val application = UIApplication.sharedApplication
    if (application.canOpenURL(settingsUrl)) {
        application.openURL(settingsUrl, emptyMap<Any?, Any?>(), null)
    } else throw CannotOpenSettingsException()
}

internal fun openAppSettingsPage() {
    openNSUrl(UIApplicationOpenSettingsURLString)
}