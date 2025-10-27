package net.thechance.mena.identity.presentation.screen.imageCropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

interface ImageCropperComponentInteractionListener {

    fun zoomBy(gestureZoom: Float, pan: Offset)

    fun zoomIn(value: Float)

    fun zoomOut(value: Float)

    fun resetAndUpdateImageSize(imageSize: IntSize)

    fun reset()

    fun updateComponentSize(componentSize: IntSize)

    fun cropToBitmap(
        painter: Painter,
        density: Density,
        layoutDirection: LayoutDirection
    )

    fun onUploadAnotherImageClicked(imageBitmap: ImageBitmap)
}