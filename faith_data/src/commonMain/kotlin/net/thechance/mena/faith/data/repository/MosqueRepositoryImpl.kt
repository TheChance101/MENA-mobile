package net.thechance.mena.faith.data.repository

import net.thechance.mena.faith.domain.entity.Mosque
import net.thechance.mena.faith.domain.repository.MosqueRepository

class MosqueRepositoryImpl() : MosqueRepository {
    override suspend fun addMosque(mosque: Mosque) {
        // TODO("Not yet implemented")
    }

    override suspend fun getNearbyMosques(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): List<Mosque> {
        return emptyList()
    }

    override suspend fun getMosquesByName(query: String): List<Mosque> {
        return emptyList()
    }
}
