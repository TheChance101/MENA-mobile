package net.thechance.mena.faith.presentation.map

import org.osmdroid.util.GeoPoint
import kotlin.math.*

object MapUtils {

    fun calculateDistance(p1: GeoPoint, p2: GeoPoint): Double {
        val lat1 = Math.toRadians(p1.latitude)
        val lon1 = Math.toRadians(p1.longitude)
        val lat2 = Math.toRadians(p2.latitude)
        val lon2 = Math.toRadians(p2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return MapConstants.EARTH_RADIUS_METERS * c
    }

    fun getClusterDistance(zoomLevel: Double): Double {
        return when {
            zoomLevel < 4.0 -> 100000.0
            zoomLevel < 6.0 -> 50000.0
            zoomLevel < 8.0 -> 20000.0
            zoomLevel < 10.0 -> 8000.0
            zoomLevel < 12.0 -> 3000.0
            zoomLevel < 14.0 -> 1000.0
            zoomLevel < 15.0 -> 500.0
            zoomLevel < 16.0 -> 200.0
            else -> 0.0
        }
    }
}