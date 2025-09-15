package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createDukan.StyleUiState

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onButtonClicked() {}
    override fun onBackClicked() {}
    override fun onColorClicked(color: Long){}
    override fun onStyleClicked(style: StyleUiState) {}
    override fun onClickUploadImage() {}
    override fun onClickEditImage() {}
    override fun onCLickNext() {}
    override fun onSaveClicked() {}
    override fun onZoomInClicked() {}
    override fun onZoomOutClicked() {}
    override fun onResetClicked() {}
    override fun onUploadAnotherImageClicked() {}
}