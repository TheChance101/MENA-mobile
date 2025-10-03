package net.thechance.mena.trends.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.trends.domain.entity.Reel
import net.thechance.mena.trends.domain.entity.UploadReelProgress

interface ReelsRepository {
    suspend fun deleteReelById(id: String)
    suspend fun getAllReels(pageNumber: Int): List<Reel>
    suspend fun updateReelById(id: String, description: String, categoryIds: List<String>)
    fun uploadReel(
        name: String,
        mimeType: String,
        size: Long,
        bytes: ByteArray,
        extension: String
    ): Flow <UploadReelProgress>
    suspend fun uploadReelThumbnail(
        thumbnail: ByteArray,
        size: Long,
        mimeType: String,
        name: String,
        extension: String,
        id: String
    )
}