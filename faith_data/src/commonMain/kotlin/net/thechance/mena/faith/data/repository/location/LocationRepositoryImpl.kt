package net.thechance.mena.faith.data.repository.location

import dev.jordond.compass.Coordinates
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val geocoder: Geocoder = MobileGeocoder(),
) : LocationRepository {

    override suspend fun getLocationName(
        coordinates: Mosque.Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(
            Coordinates(
                latitude = coordinates.latitude,
                longitude = coordinates.longitude
            )
        )
        return geocoder?.let { place ->
            listOfNotNull(place.subAdministrativeArea, place.administrativeArea, place.country)
                .joinToString(", ")
        } ?: throw FaithException.AddressNotFoundException
    }
}