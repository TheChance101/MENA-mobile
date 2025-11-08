package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CalculateDistanceUseCase {

    operator fun invoke(
        firstLocation: Mosque.Coordinates,
        secondLocation: Mosque.Coordinates,
    ): Double {

        if (firstLocation.latitude == secondLocation.latitude && firstLocation.longitude == secondLocation.longitude) return 0.0

        if (!isValidCoordinate(
                firstLocation.latitude,
                firstLocation.longitude
            ) || !isValidCoordinate(
                secondLocation.latitude,
                secondLocation.longitude
            )
        ) {
            throw FaithException.InvalidCoordinates
        }

        val earthRadiusKm = 6371.0

        val dLat = toRadians(degrees = secondLocation.latitude - firstLocation.latitude)
        val dLon = toRadians(degrees = secondLocation.longitude - firstLocation.longitude)

        val haversineComponent = sin(dLat / 2).pow(2.0) +
                cos(x = toRadians(degrees = firstLocation.latitude)) *
                cos(x = toRadians(degrees = secondLocation.latitude)) *
                sin(x = dLon / 2).pow(x = 2.0)

        val centralAngle = 2 * atan2(sqrt(x = haversineComponent), x = sqrt(1 - haversineComponent))

        return (earthRadiusKm * centralAngle).coerceAtLeast(minimumValue = 0.0)
    }

    private fun toRadians(degrees: Double): Double = degrees * (PI / 180)

    private fun isValidCoordinate(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}

