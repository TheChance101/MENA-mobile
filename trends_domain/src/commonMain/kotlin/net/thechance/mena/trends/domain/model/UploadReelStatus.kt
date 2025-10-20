package net.thechance.mena.trends.domain.model


sealed interface UploadReelStatus {

    data class UploadReelProgress(
        val numberOfUploadedBytes: Long,
        val totalBytes: Long
    ) : UploadReelStatus

    data class UploadReelSuccess(
        val reelId: String
    ) : UploadReelStatus
}