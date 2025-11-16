package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

interface ImageCropperComponentInteractionListener {

    fun zoomBy(gestureZoom: Float, pan: Offset)

    fun zoomIn(value: Float)

    fun zoomOut(value: Float)

    fun resetAndUpdateImageSize(imageSize: IntSize)

    fun reset()

    fun updateComponentSize(componentSize: IntSize)

    fun saveImageToGallery(imageByteArray: ByteArray)

    fun onUploadAnotherImageClicked(imageByteArray: ByteArray)
}