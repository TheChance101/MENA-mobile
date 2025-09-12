package net.thechance.mena.dukan.domain.repository

import net.thechance.mena.dukan.domain.entity.Dukan

interface LocationRepository {

    suspend fun getCurrentLocation(): Dukan.Location
}