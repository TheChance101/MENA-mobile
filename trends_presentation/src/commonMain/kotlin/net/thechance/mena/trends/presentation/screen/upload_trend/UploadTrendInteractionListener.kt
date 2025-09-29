package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.model.FileUiState

interface UploadTrendInteractionListener {
    fun onBackClick()
    fun onRetrieveVideo(
        file: FileUiState,
        readBytes: suspend () -> ByteArray
    )
    fun onEditVideoClick()
    fun onCancelUploadClick()
    fun onDeleteVideoClick()
    fun onRetryUploadClick()
    fun onNextClick()
}