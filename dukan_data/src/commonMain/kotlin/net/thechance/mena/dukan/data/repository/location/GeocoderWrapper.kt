package net.thechance.mena.dukan.data.repository.location

import dev.jordond.compass.Place
import net.thechance.mena.dukan.domain.entity.Dukan

interface GeocoderWrapper {
    suspend fun placeOrNull(coordinates: Dukan.Coordinates): Place?
}