package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import net.thechance.mena.identity.domain.entity.Coordinates
import net.thechance.mena.identity.domain.exception.InvalidLocationException
import net.thechance.mena.identity.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val geocoder: GeocoderWrapper,
) : LocationRepository {

    override suspend fun getCurrentLocation(): Coordinates {
        val geolocator: Geolocator = MobileGeolocator()
        return when (val result = geolocator.current()) {
            is GeolocatorResult.Error -> {
                throw InvalidLocationException("Failed to get location: ${result.message}")
            }

            is GeolocatorResult.Success -> {
                val location = result.data
                Coordinates(location.coordinates.latitude, location.coordinates.longitude)
            }
        }
    }

    override suspend fun getLocationName(
        coordinates: Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(coordinates)
        return geocoder?.let {
            listOfNotNull(it.subAdministrativeArea, it.administrativeArea, it.country)
                .joinToString(", ")
        }.orEmpty()
    }
}