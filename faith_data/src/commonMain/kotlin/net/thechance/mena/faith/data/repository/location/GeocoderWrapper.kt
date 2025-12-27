package net.thechance.mena.faith.data.repository.location

import dev.jordond.compass.Place
import net.thechance.mena.faith.domain.entity.Mosque

interface GeocoderWrapper {
    suspend fun placeOrNull(coordinates: Mosque.Coordinates): Place?
}