package net.thechance.mena.dukan.data.repository

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.currentLocationOrNull
import dev.jordond.compass.geolocation.mobile
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

class LocationRepositoryImpl : LocationRepository {
    override suspend fun getCurrentLocation(): Dukan.Coordinates? {
        val geolocator: Geolocator = Geolocator.mobile()
        val currentLocation: Location? = geolocator.currentLocationOrNull()
        return currentLocation?.coordinates?.let { current ->
            Dukan.Coordinates(latitude = current.latitude, longitude = current.longitude)
        }
    }

    override suspend fun getCurrentLocationName(
        coordinates: Dukan.Coordinates,
    ): String {
        return MobileGeocoder().placeOrNull(
            Coordinates(
                coordinates.latitude,
                coordinates.longitude
            )
        )?.locality.toString()
    }
}