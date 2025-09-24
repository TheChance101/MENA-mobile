package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.Reel

interface ReelsRepository {
    suspend fun deleteReelById(id: String)
    suspend fun getAllReels(pageNumber : Int): List<Reel>
}