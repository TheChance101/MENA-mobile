package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.UploadReelProgress
import kotlinx.coroutines.flow.Flow

interface UploadReelsRepository {
    fun uploadReel(
        name: String,
        mimeType: String,
        size: Long,
        bytes: ByteArray
    ): Flow <UploadReelProgress>
}