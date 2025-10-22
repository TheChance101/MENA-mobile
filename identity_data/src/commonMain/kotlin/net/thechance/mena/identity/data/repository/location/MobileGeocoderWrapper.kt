package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import net.thechance.mena.identity.domain.model.Coordinates as DomainCoordinates

class MobileGeocoderWrapper : GeocoderWrapper {
    private val geocoder = MobileGeocoder()

    override suspend fun placeOrNull(coordinates: DomainCoordinates): Place? {
        return geocoder.placeOrNull(Coordinates(coordinates.latitude, coordinates.longitude))
    }
}