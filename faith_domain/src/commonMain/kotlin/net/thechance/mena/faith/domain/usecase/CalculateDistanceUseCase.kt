package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class CalculateDistanceUseCase {

    operator fun invoke(
        firstLocation: Pair<Double, Double>,
        secondLocation: Pair<Double, Double>,

        ): Double {

        if (firstLocation.first == secondLocation.first && firstLocation.second == secondLocation.second) return 0.0

        if (!isValidCoordinate(firstLocation.first, firstLocation.second) || !isValidCoordinate(
                secondLocation.first,
                secondLocation.second
            )
        ) {
            throw FaithException.InvalidCoordinates
        }

        val earthRadiusKm = 6371.0

        val dLat = toRadians(degrees = secondLocation.first - firstLocation.first)
        val dLon = toRadians(degrees = secondLocation.second - firstLocation.second)

        val haversineComponent = sin(dLat / 2).pow(2.0) +
                cos(x = toRadians(degrees = firstLocation.first)) *
                cos(x = toRadians(degrees = secondLocation.first)) *
                sin(x = dLon / 2).pow(x = 2.0)

        val centralAngle = 2 * atan2(sqrt(x = haversineComponent), x = sqrt(1 - haversineComponent))

        return (earthRadiusKm * centralAngle).coerceAtLeast(minimumValue = 0.0)
    }

    private fun toRadians(degrees: Double): Double = degrees * (PI / 180)

    private fun isValidCoordinate(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}

