package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.base.ErrorState

data class UploadTrendsScreenState(
    val selectedFileMeta: SelectedFileMeta = SelectedFileMeta(),
    val uploadingState: UploadingState = UploadingState.IDLE,
    val uploadingProgress: String = "",
    val uploadedMegaBytes: String = "",
    val isNextButtonEnabled: Boolean = false,
    val errorState: ErrorState? = null
) {
    data class SelectedFileMeta(
        val name: String = "",
        val extension: String = "",
        val duration: String = "",
        val sizeInBytes: Long = 0L,
        val size: String = "",
        val bytes: ByteArray = ByteArray(0),
    )

    enum class UploadingState {
        IDLE,
        UPLOADING,
        FAILED,
        SUCCESS
    }
}