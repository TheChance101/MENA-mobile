package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState
import net.thechance.mena.trends.presentation.shared.util.isFailed
import net.thechance.mena.trends.presentation.shared.util.isIdle

data class UploadTrendScreenState(
    val trendId: String? = null,
    val selectedFile: FileUiState = FileUiState(),
    val uploadingState: UploadingTrendState = UploadingTrendState.IDLE,
    val thumbnail: ByteArray? = null,
    val uploadingProgress: Float = 0f,
    val sizeUploaded: String = "",
    val isNextButtonEnabled: Boolean = false,
    val isNextButtonLoading: Boolean = false,
    val isThumbnailLoading: Boolean = false,
    val errorState: ErrorState? = null
) {
    val isUploadVideoCardEnabled: Boolean
        get() = (uploadingState.isIdle || uploadingState.isFailed) && isThumbnailLoading.not()

    enum class UploadingTrendState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }
}