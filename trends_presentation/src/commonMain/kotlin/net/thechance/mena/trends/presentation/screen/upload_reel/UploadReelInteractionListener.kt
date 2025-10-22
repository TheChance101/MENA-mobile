package net.thechance.mena.trends.presentation.screen.upload_reel

import net.thechance.mena.trends.presentation.shared.model.FileUiState

interface UploadReelInteractionListener {
    fun onClickBack()
    fun onRetrieveVideo(file: FileUiState)
    fun onClickCancelUpload()
    fun onClickDeleteVideo()
    fun onClickRetryUpload()
    fun onClickNext()
}