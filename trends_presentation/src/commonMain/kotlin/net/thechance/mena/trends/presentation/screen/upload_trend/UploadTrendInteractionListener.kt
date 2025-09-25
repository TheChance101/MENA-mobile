package net.thechance.mena.trends.presentation.screen.upload_trend

interface UploadTrendInteractionListener {
    fun onBackClick()
    fun onUploadFileClick()
    fun onSelectFile(
        file: UploadTrendsScreenState.SelectedFileMeta,
        readBytes: suspend () -> ByteArray
    )
    fun onCancelUploadClick()
    fun onEditClick()
    fun onDeleteClick()
    fun onRetryClick()
    fun onNextClick()
}