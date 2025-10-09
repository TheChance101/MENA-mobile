package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Coordinates

interface LocationRepository {
    suspend fun getCurrentLocation(): Coordinates?
    suspend fun getLocationName(coordinates: Coordinates): String
}