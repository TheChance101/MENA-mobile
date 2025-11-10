package net.thechance.mena.faith.presentation.utils.extentions.imageCrop

import androidx.compose.ui.geometry.Rect
import com.attafitamim.krop.core.crop.ImgTransform

fun computeImageBounds(
    imageRect: Rect,
    transform: ImgTransform
): Rect {
    val scaleX = transform.scale.x
    val scaleY = transform.scale.y
    val centerX = (imageRect.left + imageRect.right) / 2f
    val centerY = (imageRect.top + imageRect.bottom) / 2f

    val newWidth = imageRect.width * scaleX
    val newHeight = imageRect.height * scaleY

    return Rect(
        left = centerX - newWidth / 2f,
        top = centerY - newHeight / 2f,
        right = centerX + newWidth / 2f,
        bottom = centerY + newHeight / 2f
    )
}