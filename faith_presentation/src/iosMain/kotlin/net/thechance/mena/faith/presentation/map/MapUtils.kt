package net.thechance.mena.faith.presentation.map

import net.thechance.mena.faith.presentation.feature.mosque.Coordinate
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MapUtils {

    fun calculateDistance(firstCoordinate: Coordinate, secondCoordinate: Coordinate): Double {
        val latRadians1 = firstCoordinate.latitude * PI / 180.0
        val longRadians1 = firstCoordinate.longitude * PI / 180.0
        val latRadians2 = secondCoordinate.latitude * PI / 180.0
        val longRadians2 = secondCoordinate.longitude * PI / 180.0

        val deltaLat = latRadians2 - latRadians1
        val deltaLong = longRadians2 - longRadians1

        val haversineOfHalfAngle = sin(deltaLat * 0.5) * sin(deltaLat * 0.5) +
                cos(latRadians1) * cos(latRadians2) *
                sin(deltaLong * 0.5) * sin(deltaLong * 0.5)
        val angularDistanceInRadians =
            2.0 * atan2(sqrt(haversineOfHalfAngle), sqrt(1.0 - haversineOfHalfAngle))

        return MapConstants.EARTH_RADIUS_METERS * angularDistanceInRadians
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