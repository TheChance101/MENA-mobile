package net.thechance.mena.trends.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.entity.UploadReelProgress

interface ReelsRepository {
    suspend fun deleteReelById(id: String)
    suspend fun getAllCurrentUserReels(pageNumber: Int): List<Reel>
    suspend fun updateReelById(id: String, description: String, categoryIds: List<String>)
    suspend fun getFeedReels(page: Int, reelId: String? = null): List<Reel>
    fun uploadReel(filePath: String, fileName: String, size: Long): Flow <UploadReelProgress>
    suspend fun uploadReelThumbnail(reelId: String, fileName: String, thumbnail: ByteArray)
    suspend fun getReelDuration(filePath: String): Long?
    suspend fun getReelThumbnail(filePath: String, timeMs: Long = 0L): ByteArray?
}