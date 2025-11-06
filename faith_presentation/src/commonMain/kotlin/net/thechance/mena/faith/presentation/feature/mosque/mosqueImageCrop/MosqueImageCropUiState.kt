package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop

import com.attafitamim.krop.core.crop.ImageCropper
import com.attafitamim.krop.core.crop.imageCropper

internal data class MosqueImageCropUiState(
    val cropper: ImageCropper = imageCropper(),
) {
    companion object Companion {
        const val MIN_ZOOM = 1f
        const val MAX_ZOOM = 5f
    }
}