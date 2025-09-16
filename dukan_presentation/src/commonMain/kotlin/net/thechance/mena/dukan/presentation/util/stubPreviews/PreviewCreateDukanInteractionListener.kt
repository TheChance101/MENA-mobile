package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanInteractionListener

object PreviewCreateDukanInteractionListener : CreateDukanInteractionListener {
    override fun onButtonClicked() {}
    override fun onBackClicked() {}
    override fun onClickUploadImage(image: ImageSrc) {}
    override fun onClickEditImage() {}
    override fun onCLickNext() {}
    override fun onImageCrop(image: ImageBitmap) {}
    override fun onCancelCrop() {}
}