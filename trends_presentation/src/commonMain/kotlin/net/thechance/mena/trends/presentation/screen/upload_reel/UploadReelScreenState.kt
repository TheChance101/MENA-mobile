package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.isFailed
import net.thechance.mena.trends.presentation.shared.util.isIdle

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
        get() = uploadingState.isIdle || uploadingState.isFailed

    enum class UploadingReelState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }
}