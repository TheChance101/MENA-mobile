package net.thechance.mena.identity.data.repository.location

import net.thechance.mena.identity.domain.entity.Coordinates
import net.thechance.mena.identity.domain.repository.MobileLocationRepository


expect class MobileLocationRepositoryImpl : MobileLocationRepository {
    override suspend fun getCurrentLocation(): Coordinates?
    override suspend fun getLocationName(coordinates: Coordinates): String
}