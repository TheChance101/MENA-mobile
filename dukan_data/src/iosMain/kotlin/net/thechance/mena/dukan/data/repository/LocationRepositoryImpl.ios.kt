package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

actual class LocationRepositoryImpl :
    LocationRepository {
    actual override suspend fun getCurrentLocation(): Dukan.Location {
        return Dukan.Location(0.0, 0.0, "")
    }
}