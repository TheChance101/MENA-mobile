package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Mosque

interface MosqueRepository {
    suspend fun addMosque(mosque: Mosque)
    suspend fun getNearbyMosques(
        latitude: Double,
        longitude: Double,
        radius: Double,
    ): List<Mosque>
}
