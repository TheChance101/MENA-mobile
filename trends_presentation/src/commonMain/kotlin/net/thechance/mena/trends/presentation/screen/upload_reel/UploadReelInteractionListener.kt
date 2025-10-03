package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.model.FileUiState

interface UploadReelInteractionListener {
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