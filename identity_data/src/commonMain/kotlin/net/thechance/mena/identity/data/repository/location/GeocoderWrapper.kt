package net.thechance.mena.identity.data.repository.location

import dev.jordond.compass.Place
import net.thechance.mena.identity.domain.model.Coordinates

interface GeocoderWrapper {
    suspend fun placeOrNull(coordinates: Coordinates): Place?
}