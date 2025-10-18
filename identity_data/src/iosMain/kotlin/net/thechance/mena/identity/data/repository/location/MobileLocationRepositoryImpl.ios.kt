package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import net.thechance.mena.identity.domain.util.Coordinates
import net.thechance.mena.identity.domain.exception.AddressNotFoundException
import net.thechance.mena.identity.domain.exception.UnableToFindLocationException
import net.thechance.mena.identity.domain.repository.MobileLocationRepository

actual class MobileLocationRepositoryImpl( private val geocoder: GeocoderWrapper,
    ) : MobileLocationRepository {

    actual override suspend fun getCurrentLocation(): Coordinates? {
        val geolocator: Geolocator = MobileGeolocator()
        return when (val result = geolocator.current()) {
            is GeolocatorResult.Error -> {
                throw UnableToFindLocationException()
            }

            is GeolocatorResult.Success -> {
                val location = result.data
                Coordinates(
                    latitude = location.coordinates.latitude,
                    longitude = location.coordinates.longitude
                )
            }
        }
    }

    actual override suspend fun getLocationName(
        coordinates: Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(coordinates)
        return geocoder?.let {
            listOfNotNull(it.subAdministrativeArea, it.administrativeArea, it.country)
                .joinToString(", ")
        } ?: throw AddressNotFoundException()
    }
}