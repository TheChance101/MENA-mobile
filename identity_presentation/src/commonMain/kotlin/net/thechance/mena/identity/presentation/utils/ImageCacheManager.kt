package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap

interface ImageCacheManager {
    fun getCachedImage(key: String): ImageBitmap?
    fun cacheImage(key: String, imageBitmap: ImageBitmap)
    fun removeCachedImage(key: String)
}