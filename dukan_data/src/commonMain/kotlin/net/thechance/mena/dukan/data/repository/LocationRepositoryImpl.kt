package net.thechance.mena.dukan.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

class LocationRepositoryImpl : LocationRepository {

    override suspend fun getCurrentLocationName(
        coordinates: Dukan.Coordinates,
    ): String {
        val geocoder = MobileGeocoder().placeOrNull(
            Coordinates(
                coordinates.latitude,
                coordinates.longitude
            )
        )
        println("This is just test for the location goeCoder $geocoder")
        return geocoder?.let {
            "${it.subAdministrativeArea}, ${it.administrativeArea}, ${it.country}"
        } ?: ""
    }
}