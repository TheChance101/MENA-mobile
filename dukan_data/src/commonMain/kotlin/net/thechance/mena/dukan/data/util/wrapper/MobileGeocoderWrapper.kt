package net.thechance.mena.dukan.data.util.wrapper

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geocoder.placeOrNull
import net.thechance.mena.dukan.domain.entity.Dukan

class MobileGeocoderWrapper : GeocoderWrapper {
    private val geocoder = MobileGeocoder()

    override suspend fun placeOrNull(coordinates: Dukan.Coordinates): Place? {
        return geocoder.placeOrNull(Coordinates(coordinates.latitude, coordinates.longitude))
    }
}