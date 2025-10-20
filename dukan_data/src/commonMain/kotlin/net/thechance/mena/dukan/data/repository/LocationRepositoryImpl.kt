package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.data.util.wrapper.GeocoderWrapper
import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val geocoder: GeocoderWrapper
) : LocationRepository {

    override suspend fun getCurrentLocationName(
        coordinates: Dukan.Coordinates,
    ): String {
        val geocoder = geocoder.placeOrNull(coordinates)
        return geocoder?.let {
            listOfNotNull(it.subAdministrativeArea, it.administrativeArea, it.country)
                .joinToString(", ")
        }.orEmpty()
    }
}