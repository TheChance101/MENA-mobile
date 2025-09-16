package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc

interface CreateDukanInteractionListener {

    fun onButtonClicked()
    fun onBackClicked()
    fun onColorClicked(color: Long)
    fun onStyleClicked(style: StyleUiState)
    fun onClickUploadImage()
    fun onClickUploadImage(image: ImageSrc)
    fun onClickEditImage()
    fun onCLickNext()
    fun onImageCrop(image: ImageBitmap)
    fun onCancelCrop()
}