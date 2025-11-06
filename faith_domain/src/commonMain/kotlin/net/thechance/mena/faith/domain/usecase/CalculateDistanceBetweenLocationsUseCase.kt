package net.thechance.mena.faith.domain.usecase

import net.thechance.mena.faith.domain.entity.Location
import kotlin.math.*

class CalculateDistanceBetweenLocationsUseCase {

    fun execute(userLocation: Location, mosqueLocation: Location): Double {
        validateCoordinates(userLocation)
        validateCoordinates(mosqueLocation)

        val earthRadiusKm = 6371.0

        val latitudeDifference = (mosqueLocation.latitude - userLocation.latitude).toRadians()
        val longitudeDifference = (mosqueLocation.longitude - userLocation.longitude).toRadians()

        val userLatitudeRadians = userLocation.latitude.toRadians()
        val mosqueLatitudeRadians = mosqueLocation.latitude.toRadians()

        val haversineA = sin(latitudeDifference / 2).pow(2.0) +
                cos(userLatitudeRadians) * cos(mosqueLatitudeRadians) * sin(longitudeDifference / 2).pow(2.0)

        val haversineC = 2 * atan2(sqrt(haversineA), sqrt(1 - haversineA))

        return earthRadiusKm * haversineC
    }

    private fun validateCoordinates(location: Location) {
        require(location.latitude in -90.0..90.0)
        require(location.longitude in -180.0..180.0)
    }

    private fun Double.toRadians(): Double = this * PI / 180.0
}

