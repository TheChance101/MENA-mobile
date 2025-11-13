package net.thechance.mena.faith.presentation.utils.extentions.imageCrop

import androidx.compose.ui.geometry.Rect

fun Rect.toAspectRatio(ratio: Float): Rect {
    if (ratio <= 0f) return this

    val currentRatio = width / height
    return if (currentRatio > ratio) {
        val newWidth = height * ratio
        val dx = (width - newWidth) * 0.5f
        Rect(left + dx, top, right - dx, bottom)
    } else {
        val newHeight = width / ratio
        val dy = (height - newHeight) * 0.5f
        Rect(left, top + dy, right, bottom - dy)
    }
}