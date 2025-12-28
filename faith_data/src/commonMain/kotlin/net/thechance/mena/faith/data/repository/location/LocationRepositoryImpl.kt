package net.thechance.mena.faith.data.repository.location

import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.faith.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val geocoder: GeocoderWrapper,
) : LocationRepository  {

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