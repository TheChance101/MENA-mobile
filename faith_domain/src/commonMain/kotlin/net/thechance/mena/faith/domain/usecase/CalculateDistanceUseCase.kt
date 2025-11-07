package net.thechance.mena.faith.domain.usecase


import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.identity.domain.entity.Address
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class CalculateDistanceUseCase {

    operator fun invoke(
        address1: Address,
        address2: Address,

        ): Double {

        if (address1.latitude == address2.latitude && address1.longitude == address2.longitude) return 0.0

        if (!isValidCoordinate(address1.latitude, address1.longitude) || !isValidCoordinate(
                address2.latitude,
                address2.longitude
            )
        ) {
            throw FaithException.InvalidCoordinates
        }

        val earthRadiusKm = 6371.0

        val dLat = toRadians(degrees = address2.latitude - address1.latitude)
        val dLon = toRadians(degrees = address2.longitude - address1.longitude)

        val haversineComponent = sin(dLat / 2).pow(2.0) +
                cos(x = toRadians(degrees = address1.latitude)) *
                cos(x = toRadians(degrees = address2.latitude)) *
                sin(x = dLon / 2).pow(x = 2.0)

        val centralAngle = 2 * atan2(sqrt(x = haversineComponent), x = sqrt(1 - haversineComponent))

        return (earthRadiusKm * centralAngle).coerceAtLeast(minimumValue = 0.0)
    }

    private fun toRadians(degrees: Double): Double = degrees * (PI / 180)

    private fun isValidCoordinate(lat: Double, lon: Double): Boolean {
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}

