package net.thechance.mena.faith.data.repository.location

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import net.thechance.mena.faith.domain.entity.Mosque

class MobileGeocoderWrapper : GeocoderWrapper {
    private val geocoder = MobileGeocoder()

    override suspend fun placeOrNull(coordinates: Mosque.Coordinates): Place? {
        return geocoder.placeOrNull(Coordinates(coordinates.latitude, coordinates.longitude))
    }
}