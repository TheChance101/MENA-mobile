package net.thechance.mena.identity.data.dataSource.local.memory

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSCache
import platform.Foundation.NSString
import platform.Foundation.create

@OptIn(BetaInteropApi::class)
actual class ImageCacheManager {

    private val cache = NSCache()

    actual fun getCachedImage(key: String): ByteArray? {
        val nsKey: NSString = NSString.Companion.create(string = key)
        return cache.objectForKey(nsKey ) as ByteArray?
    }

    actual fun cacheImage(key: String, imageByteArray: ByteArray) {
        val nsKey: NSString = NSString.Companion.create(string = key)
        cache.setObject(imageByteArray, forKey = nsKey)
    }

    actual fun removeCachedImage(key: String) {
        val nsKey: NSString = NSString.Companion.create(string = key)
        cache.removeObjectForKey(nsKey)
    }
}