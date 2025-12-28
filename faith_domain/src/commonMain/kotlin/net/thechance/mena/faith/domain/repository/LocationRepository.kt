package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Mosque

interface LocationRepository {
    suspend fun getLocationName(coordinates: Mosque.Coordinates): String
}