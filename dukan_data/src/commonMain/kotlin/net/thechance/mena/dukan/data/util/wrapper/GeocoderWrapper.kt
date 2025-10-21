package net.thechance.mena.dukan.data.util.wrapper

import dev.jordond.compass.Place
import net.thechance.mena.dukan.domain.entity.Dukan

interface GeocoderWrapper {
    suspend fun placeOrNull(coordinates: Dukan.Coordinates): Place?
}