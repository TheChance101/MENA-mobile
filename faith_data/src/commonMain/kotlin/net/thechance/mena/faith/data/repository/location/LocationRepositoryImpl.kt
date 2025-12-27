package net.thechance.mena.faith.data.repository.location

import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.MobileGeolocator
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val geocoder: GeocoderWrapper,
) : LocationRepository  {
    override suspend fun getCurrentLocation(): Mosque.Coordinates? {
        val geolocator: Geolocator = MobileGeolocator()
        return when (val result = geolocator.current(Priority.HighAccuracy)) {
            is GeolocatorResult.Error -> {
                throw FaithException.UnableToFindLocationException
            }

            is GeolocatorResult.Success -> {
                val location = result.data
                Mosque.Coordinates(
                    latitude = location.coordinates.latitude,
                    longitude = location.coordinates.longitude
                )
            }
        }
    }

    override suspend fun getLocationName(
        coordinates: Mosque.Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(coordinates)
        return geocoder?.let {
            listOfNotNull(it.subAdministrativeArea, it.administrativeArea, it.country)
                .joinToString(", ")
        } ?: throw FaithException.AddressNotFoundException
    }
}