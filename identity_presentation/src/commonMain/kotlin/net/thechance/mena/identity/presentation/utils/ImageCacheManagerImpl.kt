package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap

expect class ImageCacheManagerImpl: ImageCacheManager {

    override fun getCachedImage(key: String): ImageBitmap?

    override fun cacheImage(
        key: String,
        imageBitmap: ImageBitmap
    )

    override fun removeCachedImage(key: String)
}


