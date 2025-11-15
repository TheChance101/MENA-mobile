package net.thechance.mena.dukan.presentation.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual object MapsNavigator{
    actual fun getDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        context: Any?
    ) {
        val url = "https://www.google.com/maps/dir/?api=1" +
                "&origin=$startLat,$startLng" +
                "&destination=$endLat,$endLng" +
                "&travelmode=driving"

        val nsUrl = NSURL.URLWithString(url) ?: return

        UIApplication.sharedApplication.openURL(
            nsUrl,
            mapOf<Any?, Any>(),
            null
        )
    }

}