package net.thechance.mena.dukan.presentation.viewModel.cropImage

import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.imageCropper

data class ImageCropUiState(
    val cropper: ImageCropper = imageCropper(),
){
    companion object {
        const val MIN_ZOOM = 1f
        const val MAX_ZOOM = 5f
    }
}