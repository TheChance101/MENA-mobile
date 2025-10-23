package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap

actual class ImageCacheManagerImpl : ImageCacheManager {
    val cache = mutableMapOf<String, ImageBitmap>()

    actual override fun getCachedImage(key: String): ImageBitmap? = cache[key]

    actual override fun cacheImage(
        key: String,
        imageBitmap: ImageBitmap
    ) {
        cache.put(key,imageBitmap)
    }

    actual override fun removeCachedImage(key: String) {
        cache.remove(key)
    }
}