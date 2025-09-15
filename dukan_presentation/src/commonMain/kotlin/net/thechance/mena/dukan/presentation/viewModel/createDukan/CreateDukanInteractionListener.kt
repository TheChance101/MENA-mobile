package net.thechance.mena.dukan.presentation.viewModel.createDukan

interface CreateDukanInteractionListener {

    fun onButtonClicked()
    fun onBackClicked()
    fun onColorClicked(color: Long)
    fun onStyleClicked(style: StyleUiState)
    fun onClickUploadImage()
    fun onClickEditImage()
    fun onCLickNext()
    fun onSaveClicked()
    fun onZoomInClicked()
    fun onZoomOutClicked()
    fun onResetClicked()
    fun onUploadAnotherImageClicked()
}