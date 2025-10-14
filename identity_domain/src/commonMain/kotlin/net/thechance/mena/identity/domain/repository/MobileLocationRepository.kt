package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.Coordinates

interface MobileLocationRepository {
    suspend fun getCurrentLocation(): Coordinates?
    suspend fun getLocationName(coordinates: Coordinates): String
}