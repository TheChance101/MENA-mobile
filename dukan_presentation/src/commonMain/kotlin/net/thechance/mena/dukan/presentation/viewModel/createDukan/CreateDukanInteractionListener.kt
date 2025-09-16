package net.thechance.mena.dukan.presentation.viewModel.createDukan

import androidx.compose.ui.graphics.ImageBitmap
import com.attafitamim.krop.core.images.ImageSrc

interface CreateDukanInteractionListener {

    fun onButtonClicked()
    fun onBackClicked()
    fun onClickUploadImage(image: ImageSrc)
    fun onClickEditImage()
    fun onCLickNext()
    fun onImageCrop(image: ImageBitmap)
    fun onCancelCrop()
    fun onMapClicked(coordinates: CreateDukanUiState.CoordinatesUiState)
    fun onEditClicked()
}