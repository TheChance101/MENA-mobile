package net.thechance.mena.dukan.data.repository

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.domain.repository.LocationRepository

expect class LocationRepositoryImpl: LocationRepository {
    override suspend fun getCurrentLocation(): Dukan.Location
}