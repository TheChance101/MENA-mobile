package net.thechance.mena.trends.presentation.screen.upload_reel

import io.github.vinceglb.filekit.PlatformFile
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.model.FileUiState

data class UploadReelScreenState(
    val selectedFile: FileUiState = FileUiState(),
    val thumbnail: PlatformFile? = null,
    val uploadingTrendState: UploadingTrendState = UploadingTrendState.IDLE,
    val uploadedMegaBytes: String = "",
    val isNextButtonEnabled: Boolean = false,
    val errorState: ErrorState? = null
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