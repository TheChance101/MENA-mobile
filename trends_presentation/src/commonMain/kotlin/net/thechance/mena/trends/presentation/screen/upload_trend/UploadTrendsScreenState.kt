package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState

data class UploadTrendsScreenState(
    val selectedFile: FileUiState = FileUiState(),
    val uploadingTrendState: UploadingTrendState = UploadingTrendState.IDLE,
    val uploadedMegaBytes: String = "",
    val isNextButtonEnabled: Boolean = false,
    val errorState: ErrorState? = null
) {
    enum class UploadingTrendState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }
}