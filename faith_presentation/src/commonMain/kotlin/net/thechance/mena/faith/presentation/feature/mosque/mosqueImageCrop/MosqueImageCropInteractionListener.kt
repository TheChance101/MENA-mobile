package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop

import com.attafitamim.krop.core.images.ImageSrc

internal interface MosqueImageCropInteractionListener {
    fun onUploadAnotherImageClicked(imageSrc: ImageSrc?)
    fun onZoomInClicked()
    fun onZoomOutClicked()
    fun onResetClicked()
    fun onSaveClicked()
}