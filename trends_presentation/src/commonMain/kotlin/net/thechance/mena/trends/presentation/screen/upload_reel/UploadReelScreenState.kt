package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState

data class UploadReelScreenState(
    val reelId: String? = null,
    val selectedFile: FileUiState = FileUiState(),
    val uploadingState: UploadingReelState = UploadingReelState.IDLE,
    val thumbnail: ByteArray? = null,
    val uploadingProgress: Float = 0f,
    val sizeUploaded: String = "",
    val isNextButtonEnabled: Boolean = false,
    val isNextButtonLoading: Boolean = false,
    val errorState: ErrorState? = null
) {
    val isUploadVideoCardEnabled: Boolean
        get() = uploadingState == UploadingReelState.IDLE || uploadingState == UploadingReelState.FAILED

    enum class UploadingReelState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }
}