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
}