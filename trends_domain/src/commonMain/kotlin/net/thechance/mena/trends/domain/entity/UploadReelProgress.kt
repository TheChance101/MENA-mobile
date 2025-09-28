package net.thechance.mena.trends.domain.entity

data class UploadReelProgress(
    val reelId: String,
    val numberOfUploadedBytes: Long,
    val totalBytes: Long
)