package net.thechance.mena.faith.domain.repository

import net.thechance.mena.faith.domain.entity.Mosque

interface MosqueRepository {
    suspend fun addMosque(mosque: Mosque, imageBytes: ByteArray)

    suspend fun getNearbyMosques(
        latitude: Double,
        longitude: Double,
        radius: Double = 2.0,
    ): List<Mosque>

    suspend fun getMosquesByName(
        query: String,
        page: Int = 0,
        size: Int = 50
    ): List<Mosque>
}
