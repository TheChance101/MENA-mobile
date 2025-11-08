package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.identity.domain.entity.Address
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

class QiblahBearingCalculatorUseCase {
    private var currentContinuousAzimuth: Float = 0f
    fun calculateQiblahAngle(address: Address): Double {
        validateCoordinates(address = address)
        val userLatitudeRadians = address.latitude.toRadians()
        val userLongitudeRadians = address.longitude.toRadians()
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
    fun calculateContinuousAzimuth(rawAzimuth: Float): Float {
        val oldAngleOnCircle = currentContinuousAzimuth % 360
        val angleDifference = getShortestAngleDifference(from = oldAngleOnCircle, to = rawAzimuth)
        currentContinuousAzimuth += angleDifference
        return currentContinuousAzimuth
    }

    fun getShortestAngleDifference(from: Float, to: Float): Float {
        val targetAngle = (to - from) % 360
        return when {
            targetAngle > 180f -> targetAngle - 360f
            targetAngle < -180f -> targetAngle + 360f
            else -> targetAngle
        }
    }

    private fun validateCoordinates(address: Address) {
        require(address.latitude in -90.0..90.0) {
            throw FaithException.InvalidLatitudeException
        }
        require(address.longitude in -180.0..180.0) {
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