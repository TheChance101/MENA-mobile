package net.thechance.mena.faith.presentation.utils

import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal actual class MapNavigatorImpl : MapNavigator {
    actual override fun openMapAtCoordinate(coordinate: Coordinate) {
        val url =
            NSURL(string = "http://maps.apple.com/?ll=${coordinate.latitude},${coordinate.longitude}")
        UIApplication.sharedApplication().openURL(
            url,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
    }
}
