package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Location
import net.thechance.mena.faith.domain.exception.FaithException
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

class QiblahBearingCalculatorUseCase {
    fun calculateQiblahAngle(userLocation: Location): Double {
        validateCoordinates(location = userLocation)
        val userLatitudeRadians = userLocation.latitude.toRadians()
        val userLongitudeRadians = userLocation.longitude.toRadians()
        val kaabaLatitudeRadians = KAABA_LATITUDE.toRadians()
        val kaabaLongitudeRadians = KAABA_LONGITUDE.toRadians()

        val longitudeDifference = kaabaLongitudeRadians - userLongitudeRadians

        val y = sin(longitudeDifference) * cos(kaabaLatitudeRadians)
        val x =
            cos(userLatitudeRadians) * sin(kaabaLatitudeRadians) - sin(userLatitudeRadians) * cos(
                kaabaLatitudeRadians
            ) * cos(longitudeDifference)

        val bearing = atan2(y, x)

        val qiblaAngle = (bearing.toDegrees() + 360) % 360

        return round(qiblaAngle)
    }

    private fun validateCoordinates(location: Location) {
        require(location.latitude in -90.0..90.0) {
            throw FaithException.InvalidLatitudeException
        }
        require(location.longitude in -180.0..180.0) {
            throw FaithException.InvalidLongitudeException
        }
    }

    private fun Double.toRadians(): Double = this * PI / 180.0

    private fun Double.toDegrees(): Double = this * 180.0 / PI

    companion object Companion {
        const val KAABA_LATITUDE = 21.4225
        const val KAABA_LONGITUDE = 39.8262
    }
}