package net.thechance.mena.identity.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSCache
import platform.Foundation.NSString
import platform.Foundation.create

@OptIn(BetaInteropApi::class)
actual class ImageCacheManagerImpl : ImageCacheManager {
    private val cache = NSCache()

    actual override fun getCachedImage(key: String): ImageBitmap? {
        val nsKey: NSString = NSString.create(string = key)
        return cache.objectForKey(nsKey ) as? ImageBitmap
    }

    actual override fun cacheImage(
        key: String,
        imageBitmap: ImageBitmap
    ) {
        val nsKey: NSString = NSString.create(string = key)
        cache.setObject(imageBitmap, forKey = nsKey)
    }

    actual override fun removeCachedImage(key: String) {
        val nsKey: NSString = NSString.create(string = key)
        cache.removeObjectForKey(nsKey)
    }


}