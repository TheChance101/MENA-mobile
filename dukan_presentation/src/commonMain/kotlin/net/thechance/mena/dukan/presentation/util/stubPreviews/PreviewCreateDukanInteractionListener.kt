package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.domain.entity.Dukan
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onButtonClicked() {}
    override fun onBackClicked() {}
    override fun onColorClicked(color: Long){}
    override fun onStyleClicked(style: Dukan.Style) {}
    override fun onClickUploadImage() {}
    override fun onClickEditImage() {}
    override fun onCLickNext() {}
    override fun onSaveClicked() {}
    override fun onZoomInClicked() {}
    override fun onZoomOutClicked() {}
    override fun onResetClicked() {}
    override fun onUploadAnotherImageClicked() {}
}