package net.thechance.mena.faith.presentation.util


import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

class QiblahBearingCalculator {
    val kaabaLatitude = 21.4225
    val kaabaLongitude = 39.8262

    fun Double.toRadians(): Double = this * PI / 180.0
    fun Double.toDegrees(): Double = this * 180.0 / PI

    fun calculateQiblahAngle(userLatitude: Double, userLongitude: Double): Double {
        validateCoordinates(latitude = userLatitude, longitude = userLongitude)
        val userLatitudeRadians = userLatitude.toRadians()
        val userLongitudeRadians = userLongitude.toRadians()
        val kaabaLatitudeRadians = kaabaLatitude.toRadians()
        val kaabaLongitudeRadians = kaabaLongitude.toRadians()

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

    private fun validateCoordinates(latitude: Double, longitude: Double) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw IllegalArgumentException("Latitude must be between -90 and 90 degrees.")
        } else if (longitude < -180.0 || longitude > 180.0) {
            throw IllegalArgumentException("Longitude must be between -180 and 180 degrees.")
        }
    }
}