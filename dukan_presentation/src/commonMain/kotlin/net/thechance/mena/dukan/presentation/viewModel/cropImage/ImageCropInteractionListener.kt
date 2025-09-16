package net.thechance.mena.dukan.presentation.viewModel.cropImage

import com.attafitamim.krop.core.images.ImageSrc

interface ImageCropInteractionListener {
    fun onUploadAnotherImageClicked(imageSrc: ImageSrc?)
    fun onZoomInClicked()
    fun onZoomOutClicked()
    fun onResetClicked()
    fun onSaveClicked()
}