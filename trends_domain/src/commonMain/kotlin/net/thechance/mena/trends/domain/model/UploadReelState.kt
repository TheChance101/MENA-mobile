package net.thechance.mena.trends.domain.model


sealed interface UploadReelState {

    data class UploadReelProgress(
        val numberOfUploadedBytes: Long,
        val totalBytes: Long
    ) : UploadReelState

    data class UploadReelSuccess(
        val reelId: String
    ) : UploadReelState
}