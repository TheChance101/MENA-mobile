package net.thechance.mena.trends.domain.entity

data class UploadReelProgress(
    val reelId: String,
    val uploadedBytes: Long,
    val totalBytes: Long
)