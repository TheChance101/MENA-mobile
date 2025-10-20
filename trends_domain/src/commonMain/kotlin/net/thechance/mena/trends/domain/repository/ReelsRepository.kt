package net.thechance.mena.trends.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.model.UploadReelStatus

interface ReelsRepository {
    suspend fun deleteReelById(id: String)
    suspend fun getAllCurrentUserReels(pageNumber: Int): List<Reel>
    suspend fun updateReelById(id: String, description: String, categoryIds: List<String>)
    suspend fun getFeedReels(page: Int, reelId: String? = null): List<Reel>
    fun uploadReel(filePath: String, fileName: String, size: Long): Flow <UploadReelStatus>
    suspend fun uploadReelThumbnail(reelId: String, fileName: String, thumbnail: ByteArray)
    suspend fun getReelDuration(filePath: String): Long?
    suspend fun extractReelThumbnail(filePath: String, timeInMillis: Long = 0L): ByteArray?
    suspend fun toggleReelLike(reelId: String): Reel
    suspend fun addReelView(reelId: String)
}