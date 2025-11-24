package net.thechance.mena.trends.presentation.screen.upload_trend

import net.thechance.mena.trends.presentation.shared.model.FileUiState

interface UploadTrendInteractionListener {
    fun onClickBack()
    fun onRetrieveVideo(file: FileUiState)
    fun onClickUploadCard()
    fun onClickCancelUpload()
    fun onClickDeleteVideo()
    fun onClickRetryUpload()
    fun onClickNext()
}