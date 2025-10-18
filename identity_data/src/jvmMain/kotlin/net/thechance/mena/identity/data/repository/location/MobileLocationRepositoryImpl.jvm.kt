package net.thechance.mena.identity.data.repository.location

import net.thechance.mena.identity.domain.util.Coordinates
import net.thechance.mena.identity.domain.repository.MobileLocationRepository

actual class MobileLocationRepositoryImpl :
    MobileLocationRepository {
    actual override suspend fun getCurrentLocation(): Coordinates? {
        TODO("Not yet implemented")
    }

    actual override suspend fun getLocationName(coordinates: Coordinates): String {
        TODO("Not yet implemented")
    }
}