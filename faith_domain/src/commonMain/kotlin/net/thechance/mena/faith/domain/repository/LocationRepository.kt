package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Mosque

interface LocationRepository {
    suspend fun getCurrentLocation(): Mosque.Coordinates?
    suspend fun getLocationName(coordinates: Mosque.Coordinates): String
}