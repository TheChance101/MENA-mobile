package net.thechance.mena.faith.domain.usecase


import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.math.*


class CalculateDistanceUseCase {

    operator fun invoke(
        location1: Location,
        location2: Location,

    ): Double {

        if (location1.latitude == location2.latitude && location1.longitude == location2.longitude) return 0.0

        if (!isValidCoordinate(location1.latitude, location1.longitude) || !isValidCoordinate(location2.latitude, location2.longitude)) {
            throw FaithException.InvalidCoordinates
        }

        val earthRadiusKm = 6371.0

        val dLat = toRadians(degrees = location2.latitude - location1.latitude)
        val dLon = toRadians(degrees = location2.longitude - location1.longitude)

        val haversineComponent = sin(dLat / 2).pow(2.0) +
                cos(x = toRadians(degrees = location1.latitude)) *
                cos(x = toRadians(degrees = location2.latitude)) *
                sin(x = dLon / 2).pow(x = 2.0)

        val centralAngle = 2 * atan2(sqrt(x = haversineComponent), x = sqrt(1 - haversineComponent))

        return (earthRadiusKm * centralAngle).coerceAtLeast(minimumValue = 0.0)
    }

    private fun toRadians(degrees: Double): Double = degrees * (PI / 180)

    private fun isValidCoordinate(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}

