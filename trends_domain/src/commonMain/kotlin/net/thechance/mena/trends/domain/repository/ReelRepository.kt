package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.Reel

interface ReelRepository {
    suspend fun deleteReelById(id: Int)
    suspend fun getAllReels(): List<Reel>
}