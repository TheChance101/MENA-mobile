package net.thechance.mena.identity.data.dataSource.local.database

import android.util.LruCache

actual class ImageCacheManager {

    val cacheSizeBytes = 4 * 1024 * 1024
    val cache = LruCache<String, ByteArray>(cacheSizeBytes)

    actual fun getCachedImage(key: String): ByteArray? = cache[key]

    actual fun cacheImage(key: String, imageByteArray: ByteArray) {
        cache.put(key, imageByteArray)
    }

    actual fun removeCachedImage(key: String) {
        cache.remove(key)
    }


}