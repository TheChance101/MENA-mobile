package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState

data class UploadReelScreenState(
    val selectedFile: FileUiState = FileUiState(),
    val thumbnail: ByteArray? = null,
    val uploadingTrendState: UploadingTrendState = UploadingTrendState.IDLE,
    val uploadedBytes: Long = 0,
    val isNextButtonEnabled: Boolean = false,
    val isNextButtonLoading: Boolean = false,
    val errorState: ErrorState? = null,
    val trendId: String? = null
) {
    val isUploadVideoCardEnabled: Boolean
        get() = uploadingTrendState == UploadingTrendState.IDLE || uploadingTrendState == UploadingTrendState.FAILED

    enum class UploadingTrendState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UploadReelScreenState

        if (uploadedBytes != other.uploadedBytes) return false
        if (isNextButtonEnabled != other.isNextButtonEnabled) return false
        if (isNextButtonLoading != other.isNextButtonLoading) return false
        if (selectedFile != other.selectedFile) return false
        if (!thumbnail.contentEquals(other.thumbnail)) return false
        if (uploadingTrendState != other.uploadingTrendState) return false
        if (errorState != other.errorState) return false
        if (isUploadVideoCardEnabled != other.isUploadVideoCardEnabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uploadedBytes.hashCode()
        result = 31 * result + isNextButtonEnabled.hashCode()
        result = 31 * result + isNextButtonLoading.hashCode()
        result = 31 * result + selectedFile.hashCode()
        result = 31 * result + (thumbnail?.contentHashCode() ?: 0)
        result = 31 * result + uploadingTrendState.hashCode()
        result = 31 * result + (errorState?.hashCode() ?: 0)
        result = 31 * result + isUploadVideoCardEnabled.hashCode()
        return result
    }
}