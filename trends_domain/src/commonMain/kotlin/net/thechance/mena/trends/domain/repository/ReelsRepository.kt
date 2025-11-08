package net.thechance.mena.trends.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.model.ReelUrls
import net.thechance.mena.trends.domain.model.UploadReelProgress

interface ReelsRepository {
    suspend fun deleteReelById(id: String)
    suspend fun getAllCurrentUserReels(pageNumber: Int, reelId: String? = null): List<Reel>
    suspend fun updateReelById(id: String, description: String, categoryIds: List<String>)
    suspend fun getFeedReels(page: Int, reelId: String? = null): List<Reel>
    suspend fun uploadReel(filePath: String, size: Long): String
    fun observeUploadReelProgress(): SharedFlow<UploadReelProgress>
    suspend fun uploadReelThumbnail(reelId: String, thumbnail: ByteArray)
    suspend fun getReelDuration(filePath: String): Long?
    suspend fun extractReelThumbnail(filePath: String, timeInMillis: Long = 0L): ByteArray?
    suspend fun addReelLike(reelId: String): Reel
    suspend fun removeReelLike(reelId: String)
    suspend fun addReelView(reelId: String)
    suspend fun getReelUrls(reelId: String): ReelUrls
}